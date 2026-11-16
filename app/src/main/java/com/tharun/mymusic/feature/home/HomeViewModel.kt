package com.tharun.mymusic.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tharun.mymusic.core.model.UiState
import com.tharun.mymusic.domain.model.Playlist
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.FavoriteRepository
import com.tharun.mymusic.domain.repository.PlaybackRepository
import com.tharun.mymusic.domain.repository.PlaylistRepository
import com.tharun.mymusic.domain.usecase.GetHomeDataUseCase
import com.tharun.mymusic.domain.usecase.HomeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val data: HomeData) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val playbackRepository: PlaybackRepository,
    private val favoriteRepository: FavoriteRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getHomeDataUseCase()
                .catch { e -> _uiState.value = HomeUiState.Error(e.message ?: "Failed to load home") }
                .collect { data ->
                    _uiState.value = HomeUiState.Success(data)
                }
        }
    }

    fun playSong(song: Song, allSongs: List<Song> = emptyList()) {
        if (allSongs.isNotEmpty()) {
            val index = allSongs.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
            playbackRepository.playQueue(allSongs, index)
        } else {
            playbackRepository.playSong(song)
        }
    }

    fun playPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val songs = playlistRepository.getSongsInPlaylist(playlist.id).first()
            if (songs.isNotEmpty()) {
                playbackRepository.playQueue(songs, 0)
            }
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(song.id)
        }
    }
}
