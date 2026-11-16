package com.tharun.mymusic.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tharun.mymusic.domain.model.Album
import com.tharun.mymusic.domain.model.Artist
import com.tharun.mymusic.domain.model.Playlist
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.FavoriteRepository
import com.tharun.mymusic.domain.repository.PlaybackRepository
import com.tharun.mymusic.domain.repository.PlaylistRepository
import com.tharun.mymusic.domain.repository.SongRepository
import com.tharun.mymusic.domain.usecase.ScanDeviceAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LibraryTab {
    SONGS,
    PLAYLISTS,
    FAVORITES,
    ARTISTS
}

data class LibraryUiState(
    val selectedTab: LibraryTab = LibraryTab.SONGS,
    val songs: List<Song> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val favoriteSongs: List<Song> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val isScanning: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository,
    private val favoriteRepository: FavoriteRepository,
    private val playbackRepository: PlaybackRepository,
    private val scanDeviceAudioUseCase: ScanDeviceAudioUseCase
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(LibraryTab.SONGS)
    private data class ScanStatus(val isScanning: Boolean, val message: String?)
    private val _scanStatus = MutableStateFlow(ScanStatus(false, null))

    val uiState: StateFlow<LibraryUiState> = combine(
        _selectedTab,
        songRepository.getAllSongs(),
        playlistRepository.getAllPlaylists(),
        favoriteRepository.getFavoriteSongs(),
        _scanStatus
    ) { tab, songs, playlists, favorites, scanStatus ->
        val artists = songs.map { it.artist }.distinct().map { artistName ->
            Artist(
                id = artistName,
                name = artistName,
                imageUrl = null,
                trackCount = songs.count { s -> s.artist == artistName }
            )
        }

        LibraryUiState(
            selectedTab = tab,
            songs = songs,
            playlists = playlists,
            favoriteSongs = favorites,
            artists = artists,
            isScanning = scanStatus.isScanning,
            message = scanStatus.message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState()
    )

    fun selectTab(tab: LibraryTab) {
        _selectedTab.value = tab
    }

    fun scanDeviceFiles() {
        viewModelScope.launch {
            _scanStatus.value = ScanStatus(isScanning = true, message = null)
            val scanned = scanDeviceAudioUseCase()
            _scanStatus.value = ScanStatus(isScanning = false, message = "Scanned ${scanned.size} device audio files")
        }
    }

    fun dismissMessage() {
        _scanStatus.value = _scanStatus.value.copy(message = null)
    }

    fun playSong(song: Song, allSongs: List<Song>) {
        val playable = allSongs.filter { it.isPlayable }
        val index = playable.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        playbackRepository.playQueue(playable, index)
    }

    fun playAll(songs: List<Song>) {
        val playable = songs.filter { it.isPlayable }
        if (playable.isNotEmpty()) {
            playbackRepository.playQueue(playable, 0)
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(song.id)
        }
    }

    fun createPlaylist(name: String, description: String?) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name, description)
        }
    }
}
