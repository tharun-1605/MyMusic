package com.tharun.mymusic.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tharun.mymusic.core.model.Resource
import com.tharun.mymusic.core.model.UiState
import com.tharun.mymusic.domain.model.AudioSourceProvider
import com.tharun.mymusic.domain.model.SearchResult
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.FavoriteRepository
import com.tharun.mymusic.domain.repository.PlaybackRepository
import com.tharun.mymusic.domain.repository.SearchHistoryRepository
import com.tharun.mymusic.domain.usecase.SearchMusicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchFilter {
    ALL,
    PLAYABLE_ONLY,
    YOUTUBE_DISCOVERY
}

data class SearchUiState(
    val query: String = "",
    val filter: SearchFilter = SearchFilter.ALL,
    val result: UiState<SearchResult> = UiState.Idle,
    val recentSearches: List<String> = emptyList()
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMusicUseCase: SearchMusicUseCase,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val playbackRepository: PlaybackRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(SearchFilter.ALL)
    val selectedFilter: StateFlow<SearchFilter> = _selectedFilter.asStateFlow()

    val recentSearches: StateFlow<List<String>> = searchHistoryRepository.getRecentSearches(10)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchResultState = MutableStateFlow<UiState<SearchResult>>(UiState.Idle)
    val searchResultState: StateFlow<UiState<SearchResult>> = _searchResultState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.trim().isEmpty()) {
                        _searchResultState.value = UiState.Idle
                    } else {
                        executeSearch(query)
                    }
                }
        }
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChange(filter: SearchFilter) {
        _selectedFilter.value = filter
    }

    fun onSearchSubmit(query: String) {
        val q = query.trim()
        if (q.isNotEmpty()) {
            _searchQuery.value = q
            viewModelScope.launch {
                searchHistoryRepository.saveSearchQuery(q)
            }
            executeSearch(q)
        }
    }

    private fun executeSearch(query: String) {
        viewModelScope.launch {
            _searchResultState.value = UiState.Loading
            searchMusicUseCase(query).collect { resource ->
                _searchResultState.value = when (resource) {
                    is Resource.Success -> {
                        if (resource.data.songs.isEmpty() && resource.data.artists.isEmpty()) {
                            UiState.Empty("No results found for \"$query\"")
                        } else {
                            UiState.Success(resource.data)
                        }
                    }
                    is Resource.Error -> UiState.Error(resource.message)
                    is Resource.Loading -> UiState.Loading
                }
            }
        }
    }

    fun deleteRecentSearch(query: String) {
        viewModelScope.launch {
            searchHistoryRepository.deleteSearchQuery(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearSearchHistory()
        }
    }

    fun playSong(song: Song, allSongs: List<Song> = emptyList()) {
        if (!song.isPlayable) return
        if (allSongs.isNotEmpty()) {
            val playable = allSongs.filter { it.isPlayable }
            val index = playable.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
            playbackRepository.playQueue(playable, index)
        } else {
            playbackRepository.playSong(song)
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(song.id)
        }
    }
}
