package com.tharun.mymusic.data.repository

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.tharun.mymusic.core.dispatchers.DispatcherProvider
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatcherProvider
) : PlaybackRepository {

    private val repositoryScope = CoroutineScope(dispatchers.main + Job())
    private var controller: MediaController? = null
    private var positionUpdateJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var activeQueueSongs: MutableList<Song> = mutableListOf()

    init {
        connectToService()
    }

    private fun connectToService() {
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { command -> command.run() })
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
        if (!song.isPlayable || song.mediaUri.isNullOrBlank()) return
        playQueue(listOf(song), 0)
    }

    override fun playQueue(songs: List<Song>, startIndex: Int) {
        val playable = songs.filter { it.isPlayable && !it.mediaUri.isNullOrBlank() }
        if (playable.isEmpty()) return

        val c = controller ?: return
        activeQueueSongs.clear()
        activeQueueSongs.addAll(playable)

        val mediaItems = playable.map { it.toMediaItem() }
        c.setMediaItems(mediaItems, startIndex.coerceIn(0, playable.size - 1), 0L)
        c.prepare()
        c.play()
        syncStateFromController()
    }

    override fun resume() {
        controller?.play()
    }

    override fun pause() {
        controller?.pause()
    }

    override fun next() {
        val c = controller ?: return
        if (c.hasNextMediaItem()) {
            c.seekToNextMediaItem()
        }
    }

    override fun previous() {
        val c = controller ?: return
        if (c.currentPosition > 3000L || !c.hasPreviousMediaItem()) {
            c.seekTo(0L)
        } else {
            c.seekToPreviousMediaItem()
        }
    }

    override fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
        _playbackState.update { it.copy(currentPositionMs = positionMs) }
    }

    override fun setRepeatMode(mode: RepeatMode) {
        val c = controller ?: return
        val exoMode = when (mode) {
            RepeatMode.OFF -> Player.REPEAT_MODE_ALL
            RepeatMode.ALL -> Player.REPEAT_MODE_ONE
            RepeatMode.ONE -> Player.REPEAT_MODE_OFF
        }
        c.repeatMode = exoMode
    }

    override fun toggleShuffle() {
        val c = controller ?: return
        c.shuffleModeEnabled = !c.shuffleModeEnabled
    }

    override fun removeFromQueue(index: Int) {
        val c = controller ?: return
        if (index in 0 until activeQueueSongs.size) {
            activeQueueSongs.removeAt(index)
            c.removeMediaItem(index)
            syncStateFromController()
        }
    }

    override fun reorderQueue(fromIndex: Int, toIndex: Int) {
        val c = controller ?: return
        if (fromIndex in 0 until activeQueueSongs.size && toIndex in 0 until activeQueueSongs.size) {
            val item = activeQueueSongs.removeAt(fromIndex)
            activeQueueSongs.add(toIndex, item)
            c.moveMediaItem(fromIndex, toIndex)
            syncStateFromController()
        }
    }

    override fun clearQueue() {
        val c = controller ?: return
        activeQueueSongs.clear()
        c.clearMediaItems()
        syncStateFromController()
    }

    override fun getAudioSessionId(): Int {
        // Returned via service/AudioEffectController directly
        return 0
    }

    private fun Song.toMediaItem(): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setAlbumTitle(album)
            .setArtworkUri(artworkUri?.let { Uri.parse(it) })
            .build()

        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(mediaUri)
            .setMediaMetadata(metadata)
            .build()
    }

    private fun songFromMediaItem(item: MediaItem): Song {
        val meta = item.mediaMetadata
        return Song(
            id = item.mediaId,
            title = meta.title?.toString() ?: "Unknown",
            artist = meta.artist?.toString() ?: "Unknown Artist",
            album = meta.albumTitle?.toString() ?: "",
            artworkUri = meta.artworkUri?.toString(),
            mediaUri = item.localConfiguration?.uri?.toString(),
            durationMs = 0L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE
        )
    }
}
