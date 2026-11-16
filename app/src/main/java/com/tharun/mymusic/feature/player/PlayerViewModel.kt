package com.tharun.mymusic.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tharun.mymusic.domain.model.PlaybackState
import com.tharun.mymusic.domain.model.RepeatMode
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.FavoriteRepository
import com.tharun.mymusic.domain.repository.PlaybackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = playbackRepository.playbackState

    fun playSong(song: Song) {
        playbackRepository.playSong(song)
    }

    fun playQueue(songs: List<Song>, startIndex: Int = 0) {
        playbackRepository.playQueue(songs, startIndex)
    }

    fun togglePlayPause() {
        if (playbackState.value.isPlaying) {
            playbackRepository.pause()
        } else {
            playbackRepository.resume()
        }
    }

    fun next() = playbackRepository.next()

    fun previous() = playbackRepository.previous()

    fun seekTo(positionMs: Long) = playbackRepository.seekTo(positionMs)

    fun toggleRepeat() {
        val nextMode = when (playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        playbackRepository.setRepeatMode(nextMode)
    }

    fun toggleShuffle() = playbackRepository.toggleShuffle()

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(song.id)
        }
    }

    fun removeFromQueue(index: Int) = playbackRepository.removeFromQueue(index)

    fun reorderQueue(fromIndex: Int, toIndex: Int) = playbackRepository.reorderQueue(fromIndex, toIndex)

    fun clearQueue() = playbackRepository.clearQueue()
}
