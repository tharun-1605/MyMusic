package com.tharun.mymusic.feature.lyrics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tharun.mymusic.core.model.UiState
import com.tharun.mymusic.core.util.LrcParser
import com.tharun.mymusic.domain.model.Lyrics
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.PlaybackRepository
import com.tharun.mymusic.domain.repository.SongRepository
import com.tharun.mymusic.domain.usecase.GetLyricsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LyricsUiState(
    val song: Song? = null,
    val lyrics: Lyrics? = null,
    val activeLineIndex: Int = -1,
    val isAutoScrollPaused: Boolean = false,
    val isLoading: Boolean = true,
    val hasNoLyrics: Boolean = false
)

@HiltViewModel
class LyricsViewModel @Inject constructor(
    private val getLyricsUseCase: GetLyricsUseCase,
    private val songRepository: SongRepository,
    private val playbackRepository: PlaybackRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val songId: String = savedStateHandle.get<String>("songId") ?: ""

    private val _isAutoScrollPaused = MutableStateFlow(false)
    val isAutoScrollPaused: StateFlow<Boolean> = _isAutoScrollPaused.asStateFlow()

    val uiState: StateFlow<LyricsUiState> = combine(
        getLyricsUseCase(songId),
        songRepository.getSongById(songId),
        playbackRepository.playbackState,
        _isAutoScrollPaused
    ) { lyrics, song, playback, isPaused ->
        val activeIndex = if (lyrics != null && lyrics.isSynced) {
            LrcParser.findActiveLineIndex(lyrics.lines, playback.currentPositionMs)
        } else {
            -1
        }

        LyricsUiState(
            song = song ?: playback.currentSong,
            lyrics = lyrics,
            activeLineIndex = activeIndex,
            isAutoScrollPaused = isPaused,
            isLoading = false,
            hasNoLyrics = lyrics == null || (lyrics.lines.isEmpty() && lyrics.plainText.isNullOrBlank())
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LyricsUiState(isLoading = true)
    )

    fun onUserScrolled() {
        _isAutoScrollPaused.value = true
    }

    fun resumeAutoScroll() {
        _isAutoScrollPaused.value = false
    }

    fun seekToLine(timestampMs: Long) {
        playbackRepository.seekTo(timestampMs)
        _isAutoScrollPaused.value = false
    }
}
