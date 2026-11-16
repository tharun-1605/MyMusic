package com.tharun.mymusic.feature.playlists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tharun.mymusic.domain.model.Playlist
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.FavoriteRepository
import com.tharun.mymusic.domain.repository.PlaybackRepository
import com.tharun.mymusic.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistDetailUiState(
    val playlist: Playlist? = null,
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val playbackRepository: PlaybackRepository,
    private val favoriteRepository: FavoriteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val playlistId: Long = savedStateHandle.get<String>("playlistId")?.toLongOrNull() ?: 0L

    val uiState: StateFlow<PlaylistDetailUiState> = combine(
        playlistRepository.getPlaylistById(playlistId),
        playlistRepository.getSongsInPlaylist(playlistId)
    ) { playlist, songs ->
        PlaylistDetailUiState(
            playlist = playlist,
            songs = songs,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlaylistDetailUiState(isLoading = true)
    )

    fun playAll() {
        val songs = uiState.value.songs.filter { it.isPlayable }
        if (songs.isNotEmpty()) {
            playbackRepository.playQueue(songs, 0)
        }
    }

    fun shuffleAll() {
        val songs = uiState.value.songs.filter { it.isPlayable }
        if (songs.isNotEmpty()) {
            playbackRepository.playQueue(songs.shuffled(), 0)
        }
    }

    fun playSong(song: Song) {
        val songs = uiState.value.songs.filter { it.isPlayable }
        val index = songs.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        playbackRepository.playQueue(songs, index)
    }

    fun renamePlaylist(newName: String) {
        viewModelScope.launch {
            playlistRepository.renamePlaylist(playlistId, newName)
        }
    }

    fun deletePlaylist(onDeleted: () -> Unit) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
            onDeleted()
        }
    }

    fun removeSong(songId: String) {
        viewModelScope.launch {
            playlistRepository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun reorderSongs(songIds: List<String>) {
        viewModelScope.launch {
            playlistRepository.reorderSongsInPlaylist(playlistId, songIds)
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(song.id)
        }
    }
}
