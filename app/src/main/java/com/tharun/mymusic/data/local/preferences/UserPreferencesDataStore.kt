package com.tharun.mymusic.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tharun.mymusic.core.theme.ThemeMode
import com.tharun.mymusic.domain.model.EqualizerConfig
import com.tharun.mymusic.domain.model.EqualizerPreset
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mymusic_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    private val json = Json { ignoreUnknownKeys = true }

    object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val EQUALIZER_ENABLED = booleanPreferencesKey("equalizer_enabled")
        val EQUALIZER_PRESET = stringPreferencesKey("equalizer_preset")
        val EQUALIZER_BASS = intPreferencesKey("equalizer_bass")
        val EQUALIZER_VIRTUALIZER = intPreferencesKey("equalizer_virtualizer")
        val EQUALIZER_BANDS = stringPreferencesKey("equalizer_bands")
        val YOUTUBE_API_KEY = stringPreferencesKey("youtube_api_key")
        val LAST_PLAYED_SONG_ID = stringPreferencesKey("last_played_song_id")
        val LAST_PLAYED_POSITION = longPreferencesKey("last_played_position")
    }

    val themeMode: Flow<ThemeMode> = dataStore.data.map { preferences ->
        val modeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.DARK.name
        try {
            ThemeMode.valueOf(modeStr)
        } catch (_: Exception) {
            ThemeMode.DARK
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    val equalizerConfig: Flow<EqualizerConfig> = dataStore.data.map { preferences ->
        val isEnabled = preferences[PreferencesKeys.EQUALIZER_ENABLED] ?: false
        val presetStr = preferences[PreferencesKeys.EQUALIZER_PRESET] ?: EqualizerPreset.NORMAL.name
        val preset = try {
            EqualizerPreset.valueOf(presetStr)
        } catch (_: Exception) {
            EqualizerPreset.NORMAL
        }
        val bass = preferences[PreferencesKeys.EQUALIZER_BASS] ?: 0
        val virt = preferences[PreferencesKeys.EQUALIZER_VIRTUALIZER] ?: 0
        val bandsJson = preferences[PreferencesKeys.EQUALIZER_BANDS]
        val bandsMap: Map<Int, Int> = if (!bandsJson.isNullOrBlank()) {
            try {
                json.decodeFromString<Map<Int, Int>>(bandsJson)
            } catch (_: Exception) {
                emptyMap()
            }
        } else {
            emptyMap()
        }

        EqualizerConfig(
            isEnabled = isEnabled,
            preset = preset,
            bassBoostLevel = bass,
            virtualizerLevel = virt,
            bandLevels = bandsMap
        )
    }

    suspend fun saveEqualizerConfig(config: EqualizerConfig) {
        val bandsJson = json.encodeToString(config.bandLevels)
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EQUALIZER_ENABLED] = config.isEnabled
            preferences[PreferencesKeys.EQUALIZER_PRESET] = config.preset.name
            preferences[PreferencesKeys.EQUALIZER_BASS] = config.bassBoostLevel
            preferences[PreferencesKeys.EQUALIZER_VIRTUALIZER] = config.virtualizerLevel
            preferences[PreferencesKeys.EQUALIZER_BANDS] = bandsJson
        }
    }

    val youtubeApiKey: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.YOUTUBE_API_KEY] ?: ""
    }

    suspend fun setYoutubeApiKey(key: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.YOUTUBE_API_KEY] = key
        }
    }

    val lastPlayedSongId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_PLAYED_SONG_ID]
    }

    val lastPlayedPosition: Flow<Long> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_PLAYED_POSITION] ?: 0L
    }

    suspend fun saveLastPlayedState(songId: String, positionMs: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_PLAYED_SONG_ID] = songId
            preferences[PreferencesKeys.LAST_PLAYED_POSITION] = positionMs
        }
    }
}
