package com.tharun.mymusic.data.repository

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.tharun.mymusic.core.dispatchers.DispatcherProvider
import com.tharun.mymusic.data.local.dao.SongDao
import com.tharun.mymusic.data.local.entity.SongEntity
import com.tharun.mymusic.data.remote.sample.SamplePermittedCatalog
import com.tharun.mymusic.data.remote.stream.YouTubeStreamResolver
import com.tharun.mymusic.data.remote.youtube.YouTubeMusicInnerTubeClient
import com.tharun.mymusic.domain.model.AudioSourceProvider
import com.tharun.mymusic.domain.model.PlaybackState
import com.tharun.mymusic.domain.model.RepeatMode
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.PlaybackRepository
import com.tharun.mymusic.service.MyMusicPlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatcherProvider,
    private val streamResolver: YouTubeStreamResolver,
    private val innerTubeClient: YouTubeMusicInnerTubeClient,
    private val songDao: SongDao
) : PlaybackRepository {

    private val repositoryScope = CoroutineScope(dispatchers.main + Job())
    private var controller: MediaController? = null
    private var positionUpdateJob: Job? = null
    private var queuePreloadJob: Job? = null
    private var autoplayJob: Job? = null
    private var pendingPlayRequest: (() -> Unit)? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val activeQueueSongs = CopyOnWriteArrayList<Song>()

    init {
        connectToService()
    }

    private fun connectToService() {
        try {
            val sessionToken = SessionToken(
                context,
                ComponentName(context, MyMusicPlaybackService::class.java)
            )
            val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture.addListener({
                try {
                    controller = controllerFuture.get()
                    setupControllerListener()
                    syncStateFromController()
                    pendingPlayRequest?.invoke()
                    pendingPlayRequest = null
                } catch (e: Throwable) {
                    Log.e("PlaybackRepository", "Failed to connect to MediaController", e)
                }
            }, ContextCompat.getMainExecutor(context))
        } catch (e: Throwable) {
            Log.e("PlaybackRepository", "Failed to initialize MediaController connection", e)
        }
    }

    private fun setupControllerListener() {
        val c = controller ?: return
        c.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = playbackState == Player.STATE_BUFFERING
                _playbackState.update { it.copy(isBuffering = isBuffering) }

                // Autoplay when a song ends naturally (Spotify-like continuous autoplay)
                if (playbackState == Player.STATE_ENDED) {
                    next()
                }

                syncStateFromController()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startPositionTicker()
                } else {
                    stopPositionTicker()
                    syncStateFromController()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val idx = controller?.currentMediaItemIndex ?: -1
                if (idx in 0 until activeQueueSongs.size) {
                    val currentSong = activeQueueSongs[idx]

                    // Persist to database so recently played and recommendations update
                    repositoryScope.launch(dispatchers.io) {
                        try {
                            songDao.insertSong(SongEntity.fromDomain(currentSong))
                        } catch (_: Throwable) {}
                    }

                    // Preload the next upcoming track immediately
                    preloadUpcomingTrack(idx + 1)

                    // If approaching end of queue, fetch more related songs
                    if (idx >= activeQueueSongs.size - 2) {
                        fetchMoreRelatedSongs(currentSong)
                    }
                }
                syncStateFromController()
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                val mode = when (repeatMode) {
                    Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                    Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                    else -> RepeatMode.OFF
                }
                _playbackState.update { it.copy(repeatMode = mode) }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _playbackState.update { it.copy(isShuffleEnabled = shuffleModeEnabled) }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("PlaybackRepository", "Player error: ${error.errorCodeName}", error)
                _playbackState.update { it.copy(isBuffering = false, isPlaying = false) }
            }
        })
    }

    private fun syncStateFromController() {
        val c = controller ?: return
        val currentMediaItem = c.currentMediaItem
        val currentSong = currentMediaItem?.let { item ->
            activeQueueSongs.find { it.id == item.mediaId } ?: songFromMediaItem(item)
        }

        val repeat = when (c.repeatMode) {
            Player.REPEAT_MODE_ALL -> RepeatMode.ALL
            Player.REPEAT_MODE_ONE -> RepeatMode.ONE
            else -> RepeatMode.OFF
        }

        _playbackState.update {
            it.copy(
                currentSong = currentSong,
                isPlaying = c.isPlaying,
                isBuffering = c.playbackState == Player.STATE_BUFFERING,
                currentPositionMs = c.currentPosition.coerceAtLeast(0L),
                durationMs = c.duration.coerceAtLeast(0L),
                repeatMode = repeat,
                isShuffleEnabled = c.shuffleModeEnabled,
                queue = activeQueueSongs.toList(),
                currentQueueIndex = c.currentMediaItemIndex
            )
        }
    }

    private fun startPositionTicker() {
        positionUpdateJob?.cancel()
        positionUpdateJob = repositoryScope.launch {
            while (isActive) {
                val c = controller
                if (c != null && c.isPlaying) {
                    _playbackState.update {
                        it.copy(
                            currentPositionMs = c.currentPosition.coerceAtLeast(0L),
                            durationMs = c.duration.coerceAtLeast(0L)
                        )
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopPositionTicker() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    override fun playSong(song: Song) {
        playQueue(listOf(song), 0)
    }

    override fun playQueue(songs: List<Song>, startIndex: Int) {
        if (songs.isEmpty()) return

        val safeIndex = startIndex.coerceIn(0, songs.size - 1)
        val targetSong = songs[safeIndex]

        activeQueueSongs.clear()
        activeQueueSongs.addAll(songs)

        _playbackState.update {
            it.copy(
                currentSong = targetSong,
                isBuffering = true,
                isPlaying = false,
                queue = activeQueueSongs.toList(),
                currentQueueIndex = safeIndex
            )
        }

        if (controller == null) {
            pendingPlayRequest = { playQueue(songs, startIndex) }
            connectToService()
            return
        }

        queuePreloadJob?.cancel()
        autoplayJob?.cancel()

        repositoryScope.launch(dispatchers.io) {
            // Persist target song to DB
            try {
                songDao.insertSong(SongEntity.fromDomain(targetSong))
            } catch (_: Throwable) {}

            // 1. Resolve clicked target song's REAL audio stream
            val targetVideoId = targetSong.youtubeVideoId?.takeIf { it.isNotBlank() }
                ?: if (targetSong.id.startsWith("yt_")) targetSong.id.removePrefix("yt_") else null

            val targetStreamUrl = if (!targetVideoId.isNullOrBlank()) {
                streamResolver.resolveAudioStream(targetVideoId)
            } else {
                targetSong.mediaUri?.takeIf { it.isNotBlank() }
                    ?: streamResolver.getQuickAudioStream(targetSong.id)
            }

            val resolvedTarget = targetSong.copy(
                mediaUri = targetStreamUrl,
                isPlayable = true
            )

            val resolvedSongs = songs.toMutableList()
            resolvedSongs[safeIndex] = resolvedTarget
            activeQueueSongs[safeIndex] = resolvedTarget

            // 2. Play target song immediately on the controller
            withContext(dispatchers.main) {
                val c = controller ?: return@withContext
                val mediaItems = resolvedSongs.map { it.toMediaItem() }
                try {
                    c.setMediaItems(mediaItems, safeIndex, 0L)
                    c.prepare()
                    c.play()
                    syncStateFromController()
                } catch (e: Throwable) {
                    Log.e("PlaybackRepository", "Failed to start playback on controller", e)
                    _playbackState.update { it.copy(isBuffering = false) }
                }
            }

            // 3. Immediately pre-resolve the upcoming track (safeIndex + 1)
            preloadUpcomingTrack(safeIndex + 1)

            // 4. If queue is small (e.g. single song played from search/home), fetch related songs (Spotify-like Radio autoplay)
            if (!targetVideoId.isNullOrBlank() && songs.size <= 2) {
                fetchMoreRelatedSongs(resolvedTarget)
            }
        }
    }

    override fun resume() {
        try {
            controller?.play()
        } catch (e: Throwable) {
            Log.e("PlaybackRepository", "Resume failed", e)
        }
    }

    override fun pause() {
        try {
            controller?.pause()
        } catch (e: Throwable) {
            Log.e("PlaybackRepository", "Pause failed", e)
        }
    }

    override fun next() {
        val c = controller ?: return
        val currentIdx = c.currentMediaItemIndex
        val nextIdx = currentIdx + 1

        if (nextIdx in 0 until activeQueueSongs.size) {
            val nextSong = activeQueueSongs[nextIdx]
            val vId = nextSong.youtubeVideoId?.takeIf { it.isNotBlank() }
                ?: if (nextSong.id.startsWith("yt_")) nextSong.id.removePrefix("yt_") else null

            _playbackState.update {
                it.copy(currentSong = nextSong, isBuffering = true)
            }

            repositoryScope.launch(dispatchers.io) {
                val realStream = if (!vId.isNullOrBlank()) {
                    streamResolver.resolveAudioStream(vId)
                } else {
                    nextSong.mediaUri?.takeIf { it.isNotBlank() } ?: streamResolver.getQuickAudioStream(nextSong.id)
                }

                val updatedNext = nextSong.copy(mediaUri = realStream, isPlayable = true)
                activeQueueSongs[nextIdx] = updatedNext

                try {
                    songDao.insertSong(SongEntity.fromDomain(updatedNext))
                } catch (_: Throwable) {}

                withContext(dispatchers.main) {
                    val ctrl = controller ?: return@withContext
                    if (nextIdx < ctrl.mediaItemCount) {
                        ctrl.replaceMediaItem(nextIdx, updatedNext.toMediaItem())
                        ctrl.seekTo(nextIdx, 0L)
                    } else {
                        ctrl.addMediaItem(updatedNext.toMediaItem())
                        ctrl.seekTo(nextIdx, 0L)
                    }
                    ctrl.play()
                    syncStateFromController()
                }

                // Pre-resolve the track after this one (nextIdx + 1)
                preloadUpcomingTrack(nextIdx + 1)

                // If approaching queue end, fetch more related songs
                if (nextIdx >= activeQueueSongs.size - 2) {
                    fetchMoreRelatedSongs(updatedNext)
                }
            }
        } else {
            // End of queue reached: Autoplay related songs matching language, artist & theme!
            val currentSong = activeQueueSongs.getOrNull(currentIdx)
                ?: _playbackState.value.currentSong
            if (currentSong != null) {
                val vId = currentSong.youtubeVideoId?.takeIf { it.isNotBlank() }
                    ?: if (currentSong.id.startsWith("yt_")) currentSong.id.removePrefix("yt_") else null

                if (!vId.isNullOrBlank()) {
                    _playbackState.update { it.copy(isBuffering = true) }
                    repositoryScope.launch(dispatchers.io) {
                        val related = innerTubeClient.getRelatedSongs(vId)
                        if (related.isNotEmpty()) {
                            appendAndPlayAutoplay(related)
                        } else {
                            withContext(dispatchers.main) {
                                _playbackState.update { it.copy(isBuffering = false) }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun previous() {
        val c = controller ?: return
        if (c.currentPosition > 3000L) {
            c.seekTo(0L)
            return
        }

        val currentIdx = c.currentMediaItemIndex
        val prevIdx = currentIdx - 1
        if (prevIdx in 0 until activeQueueSongs.size) {
            val prevSong = activeQueueSongs[prevIdx]
            val vId = prevSong.youtubeVideoId?.takeIf { it.isNotBlank() }
                ?: if (prevSong.id.startsWith("yt_")) prevSong.id.removePrefix("yt_") else null

            _playbackState.update {
                it.copy(currentSong = prevSong, isBuffering = true)
            }

            repositoryScope.launch(dispatchers.io) {
                val realStream = if (!vId.isNullOrBlank()) {
                    streamResolver.resolveAudioStream(vId)
                } else {
                    prevSong.mediaUri?.takeIf { it.isNotBlank() } ?: streamResolver.getQuickAudioStream(prevSong.id)
                }
                val updatedPrev = prevSong.copy(mediaUri = realStream, isPlayable = true)
                activeQueueSongs[prevIdx] = updatedPrev

                withContext(dispatchers.main) {
                    val ctrl = controller ?: return@withContext
                    if (prevIdx < ctrl.mediaItemCount) {
                        ctrl.replaceMediaItem(prevIdx, updatedPrev.toMediaItem())
                        ctrl.seekTo(prevIdx, 0L)
                    }
                    ctrl.play()
                    syncStateFromController()
                }
            }
        } else {
            c.seekTo(0L)
        }
    }

    override fun seekTo(positionMs: Long) {
        try {
            controller?.seekTo(positionMs)
            _playbackState.update { it.copy(currentPositionMs = positionMs) }
        } catch (e: Throwable) {
            Log.e("PlaybackRepository", "Seek failed", e)
        }
    }

    override fun setRepeatMode(mode: RepeatMode) {
        try {
            val c = controller ?: return
            val exoMode = when (mode) {
                RepeatMode.OFF -> Player.REPEAT_MODE_ALL
                RepeatMode.ALL -> Player.REPEAT_MODE_ONE
                RepeatMode.ONE -> Player.REPEAT_MODE_OFF
            }
            c.repeatMode = exoMode
        } catch (e: Throwable) {
            Log.e("PlaybackRepository", "Set repeat failed", e)
        }
    }

    override fun toggleShuffle() {
        try {
            val c = controller ?: return
            c.shuffleModeEnabled = !c.shuffleModeEnabled
        } catch (e: Throwable) {
            Log.e("PlaybackRepository", "Toggle shuffle failed", e)
        }
    }

    override fun removeFromQueue(index: Int) {
        try {
            val c = controller ?: return
            if (index in 0 until activeQueueSongs.size) {
                activeQueueSongs.removeAt(index)
                if (index < c.mediaItemCount) {
                    c.removeMediaItem(index)
                }
                syncStateFromController()
            }
        } catch (e: Throwable) {
            Log.e("PlaybackRepository", "Remove from queue failed", e)
        }
    }

    override fun reorderQueue(fromIndex: Int, toIndex: Int) {
        try {
            val c = controller ?: return
            if (fromIndex in 0 until activeQueueSongs.size && toIndex in 0 until activeQueueSongs.size) {
                val item = activeQueueSongs.removeAt(fromIndex)
                activeQueueSongs.add(toIndex, item)
                if (fromIndex < c.mediaItemCount && toIndex < c.mediaItemCount) {
                    c.moveMediaItem(fromIndex, toIndex)
                }
                syncStateFromController()
            }
        } catch (e: Throwable) {
            Log.e("PlaybackRepository", "Reorder queue failed", e)
        }
    }

    override fun clearQueue() {
        try {
            val c = controller ?: return
            activeQueueSongs.clear()
            c.clearMediaItems()
            syncStateFromController()
        } catch (e: Throwable) {
            Log.e("PlaybackRepository", "Clear queue failed", e)
        }
    }

    override fun getAudioSessionId(): Int = 0

    private fun preloadUpcomingTrack(index: Int) {
        if (index !in 0 until activeQueueSongs.size) return
        val s = activeQueueSongs[index]
        val vId = s.youtubeVideoId?.takeIf { it.isNotBlank() }
            ?: if (s.id.startsWith("yt_")) s.id.removePrefix("yt_") else null

        if (!vId.isNullOrBlank() && s.mediaUri?.contains("googlevideo.com") != true) {
            repositoryScope.launch(dispatchers.io) {
                try {
                    val resolved = streamResolver.resolveAudioStream(vId)
                    if (resolved.isNotBlank() && resolved != s.mediaUri) {
                        val updated = s.copy(mediaUri = resolved, isPlayable = true)
                        activeQueueSongs[index] = updated
                        withContext(dispatchers.main) {
                            val ctrl = controller
                            if (ctrl != null && index < ctrl.mediaItemCount && index != ctrl.currentMediaItemIndex) {
                                ctrl.replaceMediaItem(index, updated.toMediaItem())
                            }
                        }
                    }
                } catch (_: Throwable) {}
            }
        }
    }

    private fun fetchMoreRelatedSongs(seedSong: Song) {
        val vId = seedSong.youtubeVideoId?.takeIf { it.isNotBlank() }
            ?: if (seedSong.id.startsWith("yt_")) seedSong.id.removePrefix("yt_") else null
        if (vId.isNullOrBlank()) return

        autoplayJob?.cancel()
        autoplayJob = repositoryScope.launch(dispatchers.io) {
            try {
                val related = innerTubeClient.getRelatedSongs(vId)
                if (related.isNotEmpty()) {
                    val existingIds = activeQueueSongs.map { it.id }.toSet()
                    val newTracks = related.filter { !existingIds.contains(it.id) }
                    if (newTracks.isNotEmpty()) {
                        activeQueueSongs.addAll(newTracks)
                        withContext(dispatchers.main) {
                            val ctrl = controller
                            if (ctrl != null) {
                                ctrl.addMediaItems(newTracks.map { it.toMediaItem() })
                            }
                            _playbackState.update { it.copy(queue = activeQueueSongs.toList()) }
                        }
                    }
                }
            } catch (_: Throwable) {}
        }
    }

    private suspend fun appendAndPlayAutoplay(related: List<Song>) {
        val existingIds = activeQueueSongs.map { it.id }.toSet()
        val newTracks = related.filter { !existingIds.contains(it.id) }
        if (newTracks.isEmpty()) return

        val firstNew = newTracks.first()
        val vId = firstNew.youtubeVideoId?.takeIf { it.isNotBlank() }
            ?: if (firstNew.id.startsWith("yt_")) firstNew.id.removePrefix("yt_") else null

        val realStream = if (!vId.isNullOrBlank()) {
            streamResolver.resolveAudioStream(vId)
        } else {
            firstNew.mediaUri ?: streamResolver.getQuickAudioStream(firstNew.id)
        }

        val resolvedFirst = firstNew.copy(mediaUri = realStream, isPlayable = true)
        val playIndex = activeQueueSongs.size
        activeQueueSongs.addAll(listOf(resolvedFirst) + newTracks.drop(1))

        try {
            songDao.insertSong(SongEntity.fromDomain(resolvedFirst))
        } catch (_: Throwable) {}

        withContext(dispatchers.main) {
            val ctrl = controller ?: return@withContext
            ctrl.addMediaItems(listOf(resolvedFirst.toMediaItem()) + newTracks.drop(1).map { it.toMediaItem() })
            ctrl.seekTo(playIndex, 0L)
            ctrl.play()
            syncStateFromController()
        }

        // Preload next upcoming related track
        preloadUpcomingTrack(playIndex + 1)
    }

    private fun Song.toMediaItem(): MediaItem {
        val uriStr = if (!mediaUri.isNullOrBlank()) {
            mediaUri
        } else {
            val vId = youtubeVideoId?.takeIf { it.isNotBlank() }
                ?: if (id.startsWith("yt_")) id.removePrefix("yt_") else null
            vId?.let { streamResolver.getCachedStream(it) } ?: streamResolver.getQuickAudioStream(id)
        }

        val parsedUri = try { Uri.parse(uriStr) } catch (_: Throwable) { Uri.parse(SamplePermittedCatalog.DEFAULT_FALLBACK_AUDIO) }

        val artUri = artworkUri?.takeIf { it.isNotBlank() }?.let {
            try { Uri.parse(it) } catch (_: Throwable) { null }
        }

        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setAlbumTitle(album)
            .setArtworkUri(artUri)
            .build()

        val reqMetadata = MediaItem.RequestMetadata.Builder()
            .setMediaUri(parsedUri)
            .build()

        return MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(metadata)
            .setUri(parsedUri)
            .setRequestMetadata(reqMetadata)
            .build()
    }

    private fun songFromMediaItem(item: MediaItem): Song {
        val meta = item.mediaMetadata
        val uriStr = item.requestMetadata.mediaUri?.toString()
            ?: item.localConfiguration?.uri?.toString()
        val vId = if (item.mediaId.startsWith("yt_")) item.mediaId.removePrefix("yt_") else null
        return Song(
            id = item.mediaId,
            title = meta.title?.toString() ?: "Unknown Track",
            artist = meta.artist?.toString() ?: "Unknown Artist",
            album = meta.albumTitle?.toString() ?: "",
            artworkUri = meta.artworkUri?.toString(),
            mediaUri = uriStr,
            durationMs = 0L,
            isPlayable = true,
            provider = if (vId != null) AudioSourceProvider.YOUTUBE_METADATA else AudioSourceProvider.SAMPLE,
            youtubeVideoId = vId
        )
    }
}
