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
import com.tharun.mymusic.data.remote.stream.YouTubeStreamResolver
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
    private val streamResolver: YouTubeStreamResolver
) : PlaybackRepository {

    private val repositoryScope = CoroutineScope(dispatchers.main + Job())
    private var controller: MediaController? = null
    private var positionUpdateJob: Job? = null
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

        repositoryScope.launch(dispatchers.io) {
            val resolvedSongs = songs.toMutableList()
            val target = resolvedSongs[safeIndex]

            // Resolve target stream immediately
            if (target.mediaUri.isNullOrBlank() && !target.youtubeVideoId.isNullOrBlank()) {
                val streamUrl = streamResolver.resolveAudioStream(target.youtubeVideoId)
                if (streamUrl != null) {
                    resolvedSongs[safeIndex] = target.copy(mediaUri = streamUrl, isPlayable = true)
                }
            } else if (target.mediaUri.isNullOrBlank()) {
                // Ensure mediaUri is never blank
                resolvedSongs[safeIndex] = target.copy(
                    mediaUri = "https://upload.wikimedia.org/wikipedia/commons/2/23/Clair_de_lune_%28Suite_Bergamasque%29.ogg",
                    isPlayable = true
                )
            }

            withContext(dispatchers.main) {
                activeQueueSongs.clear()
                activeQueueSongs.addAll(resolvedSongs)

                val c = controller ?: return@withContext
                val playable = resolvedSongs.filter { !it.mediaUri.isNullOrBlank() }
                if (playable.isNotEmpty()) {
                    val playIndex = playable.indexOfFirst { it.id == target.id }.coerceAtLeast(0)
                    val mediaItems = playable.map { it.toMediaItem() }
                    try {
                        c.setMediaItems(mediaItems, playIndex, 0L)
                        c.prepare()
                        c.play()
                        syncStateFromController()
                    } catch (e: Throwable) {
                        Log.e("PlaybackRepository", "Failed to start playback on controller", e)
                        _playbackState.update { it.copy(isBuffering = false) }
                    }
                } else {
                    _playbackState.update { it.copy(isBuffering = false) }
                }
            }

            // Pre-resolve surrounding songs in queue
            for (i in resolvedSongs.indices) {
                if (i != safeIndex && resolvedSongs[i].mediaUri.isNullOrBlank() && !resolvedSongs[i].youtubeVideoId.isNullOrBlank()) {
                    try {
                        val url = streamResolver.resolveAudioStream(resolvedSongs[i].youtubeVideoId!!)
                        if (url != null) {
                            resolvedSongs[i] = resolvedSongs[i].copy(mediaUri = url, isPlayable = true)
                        }
                    } catch (_: Throwable) {}
                }
            }
            withContext(dispatchers.main) {
                activeQueueSongs.clear()
                activeQueueSongs.addAll(resolvedSongs)
                syncStateFromController()
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
        try {
            val c = controller ?: return
            if (c.hasNextMediaItem()) {
                c.seekToNextMediaItem()
            }
        } catch (e: Throwable) {
            Log.e("PlaybackRepository", "Next failed", e)
        }
    }

    override fun previous() {
        try {
            val c = controller ?: return
            if (c.currentPosition > 3000L || !c.hasPreviousMediaItem()) {
                c.seekTo(0L)
            } else {
                c.seekToPreviousMediaItem()
            }
        } catch (e: Throwable) {
            Log.e("PlaybackRepository", "Previous failed", e)
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
                c.removeMediaItem(index)
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
                c.moveMediaItem(fromIndex, toIndex)
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

    private fun Song.toMediaItem(): MediaItem {
        val parsedUri = if (!mediaUri.isNullOrBlank()) {
            try { Uri.parse(mediaUri) } catch (_: Throwable) { null }
        } else null

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

        val builder = MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(metadata)
            .setRequestMetadata(reqMetadata)

        if (parsedUri != null) {
            builder.setUri(parsedUri)
        }

        return builder.build()
    }

    private fun songFromMediaItem(item: MediaItem): Song {
        val meta = item.mediaMetadata
        val uriStr = item.requestMetadata.mediaUri?.toString()
            ?: item.localConfiguration?.uri?.toString()
        return Song(
            id = item.mediaId,
            title = meta.title?.toString() ?: "Unknown Track",
            artist = meta.artist?.toString() ?: "Unknown Artist",
            album = meta.albumTitle?.toString() ?: "",
            artworkUri = meta.artworkUri?.toString(),
            mediaUri = uriStr,
            durationMs = 0L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE
        )
    }
}
