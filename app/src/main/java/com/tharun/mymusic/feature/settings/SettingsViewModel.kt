package com.tharun.mymusic.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tharun.mymusic.BuildConfig
import com.tharun.mymusic.core.theme.ThemeMode
import com.tharun.mymusic.data.repository.PlaylistRepositoryImpl
import com.tharun.mymusic.data.repository.SongRepositoryImpl
import com.tharun.mymusic.domain.repository.RecentlyPlayedRepository
import com.tharun.mymusic.domain.repository.SearchHistoryRepository
import com.tharun.mymusic.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val youtubeApiKey: String = "",
    val hasBuildConfigKey: Boolean = BuildConfig.YOUTUBE_API_KEY.isNotBlank(),
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val recentlyPlayedRepository: RecentlyPlayedRepository,
    private val songRepository: SongRepositoryImpl,
    private val playlistRepository: PlaylistRepositoryImpl
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.themeMode,
        preferencesRepository.youtubeApiKey,
        _message
    ) { theme, apiKey, msg ->
        SettingsUiState(
            themeMode = theme,
            youtubeApiKey = apiKey,
            hasBuildConfigKey = BuildConfig.YOUTUBE_API_KEY.isNotBlank(),
            message = msg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun saveYouTubeApiKey(key: String) {
        viewModelScope.launch {
            preferencesRepository.setYoutubeApiKey(key.trim())
            _message.value = "YouTube API key updated successfully"
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearSearchHistory()
            _message.value = "Search history cleared"
        }
    }

    fun clearRecentlyPlayed() {
        viewModelScope.launch {
            recentlyPlayedRepository.clearHistory()
            _message.value = "Playback history cleared"
        }
    }

    fun reseedSampleData() {
        viewModelScope.launch {
            songRepository.seedSampleCatalogIfEmpty()
            playlistRepository.seedSamplePlaylistsIfEmpty()
            _message.value = "Sample catalog reloaded"
        }
    }

    fun dismissMessage() {
        _message.value = null
    }
}
