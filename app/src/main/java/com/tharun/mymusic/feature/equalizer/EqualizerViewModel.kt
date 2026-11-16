package com.tharun.mymusic.feature.equalizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tharun.mymusic.domain.model.EqualizerBand
import com.tharun.mymusic.domain.model.EqualizerConfig
import com.tharun.mymusic.domain.model.EqualizerPreset
import com.tharun.mymusic.domain.repository.UserPreferencesRepository
import com.tharun.mymusic.service.AudioEffectController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EqualizerUiState(
    val config: EqualizerConfig = EqualizerConfig(),
    val bands: List<EqualizerBand> = emptyList(),
    val isSupported: Boolean = true
)

@HiltViewModel
class EqualizerViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val audioEffectController: AudioEffectController
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        EqualizerUiState(
            bands = audioEffectController.getBands(),
            isSupported = true
        )
    )
    val uiState: StateFlow<EqualizerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.equalizerConfig.collect { config ->
                _uiState.update {
                    it.copy(
                        config = config,
                        bands = audioEffectController.getBands().ifEmpty { defaultMockBands(config) }
                    )
                }
            }
        }
    }

    fun toggleEnabled(enabled: Boolean) {
        val newConfig = _uiState.value.config.copy(isEnabled = enabled)
        saveAndApply(newConfig)
    }

    fun selectPreset(preset: EqualizerPreset) {
        val newConfig = _uiState.value.config.copy(preset = preset)
        saveAndApply(newConfig)
    }

    fun setBassBoost(level: Int) {
        val newConfig = _uiState.value.config.copy(bassBoostLevel = level)
        saveAndApply(newConfig)
    }

    fun setVirtualizer(level: Int) {
        val newConfig = _uiState.value.config.copy(virtualizerLevel = level)
        saveAndApply(newConfig)
    }

    fun setBandGain(bandIndex: Int, level: Int) {
        val updatedBands = _uiState.value.config.bandLevels.toMutableMap()
        updatedBands[bandIndex] = level
        val newConfig = _uiState.value.config.copy(
            preset = EqualizerPreset.CUSTOM,
            bandLevels = updatedBands
        )
        saveAndApply(newConfig)
    }

    private fun saveAndApply(config: EqualizerConfig) {
        _uiState.update { it.copy(config = config) }
        audioEffectController.applyConfig(config)
        viewModelScope.launch {
            preferencesRepository.saveEqualizerConfig(config)
        }
    }

    private fun defaultMockBands(config: EqualizerConfig): List<EqualizerBand> {
        val freqs = listOf(60, 230, 910, 3600, 14000)
        return freqs.mapIndexed { index, freq ->
            val level = config.bandLevels[index] ?: 0
            EqualizerBand(
                index = index,
                centerFreqHz = freq,
                gainDb = level / 100f,
                minDb = -15f,
                maxDb = 15f
            )
        }
    }
}
