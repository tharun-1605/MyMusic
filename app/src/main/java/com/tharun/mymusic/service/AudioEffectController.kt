package com.tharun.mymusic.service

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.util.Log
import com.tharun.mymusic.domain.model.EqualizerBand
import com.tharun.mymusic.domain.model.EqualizerConfig
import com.tharun.mymusic.domain.model.EqualizerPreset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioEffectController @Inject constructor() {

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var currentAudioSessionId: Int = 0

    val isSupported: Boolean
        get() = equalizer != null

    fun initEffects(audioSessionId: Int, config: EqualizerConfig) {
        if (audioSessionId == 0) return
        if (currentAudioSessionId == audioSessionId && equalizer != null) return

        release()
        currentAudioSessionId = audioSessionId

        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = config.isEnabled
            }
        } catch (e: Exception) {
            Log.w("AudioEffectController", "Equalizer not supported on session $audioSessionId: ${e.message}")
            equalizer = null
        }

        try {
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = config.isEnabled
                if (strengthSupported) {
                    setStrength(config.bassBoostLevel.toShort())
                }
            }
        } catch (e: Exception) {
            Log.w("AudioEffectController", "BassBoost not supported: ${e.message}")
            bassBoost = null
        }

        try {
            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = config.isEnabled
                if (strengthSupported) {
                    setStrength(config.virtualizerLevel.toShort())
                }
            }
        } catch (e: Exception) {
            Log.w("AudioEffectController", "Virtualizer not supported: ${e.message}")
            virtualizer = null
        }

        try {
            applyConfig(config)
        } catch (e: Exception) {
            Log.w("AudioEffectController", "Error applying config on session $audioSessionId: ${e.message}")
        }
    }

    fun applyConfig(config: EqualizerConfig) {
        val eq = equalizer ?: return
        try {
            eq.enabled = config.isEnabled
            bassBoost?.enabled = config.isEnabled
            virtualizer?.enabled = config.isEnabled

            if (config.preset != EqualizerPreset.CUSTOM) {
                applyPreset(config.preset)
            } else {
                // Apply custom band levels
                val minLevel = eq.bandLevelRange[0]
                val maxLevel = eq.bandLevelRange[1]
                config.bandLevels.forEach { (bandIndex, level) ->
                    if (bandIndex in 0 until eq.numberOfBands) {
                        val clamped = level.coerceIn(minLevel.toInt(), maxLevel.toInt()).toShort()
                        eq.setBandLevel(bandIndex.toShort(), clamped)
                    }
                }
            }

            if (bassBoost?.strengthSupported == true) {
                bassBoost?.setStrength(config.bassBoostLevel.coerceIn(0, 1000).toShort())
            }
            if (virtualizer?.strengthSupported == true) {
                virtualizer?.setStrength(config.virtualizerLevel.coerceIn(0, 1000).toShort())
            }
        } catch (e: Exception) {
            Log.e("AudioEffectController", "Error applying equalizer config", e)
        }
    }

    private fun applyPreset(preset: EqualizerPreset) {
        val eq = equalizer ?: return
        val numBands = eq.numberOfBands.toInt()
        val minLevel = eq.bandLevelRange[0]
        val maxLevel = eq.bandLevelRange[1]
        val mid = 0.toShort()

        val gains: List<Float> = when (preset) {
            EqualizerPreset.NORMAL -> List(numBands) { 0f }
            EqualizerPreset.BASS_BOOST -> listOf(0.8f, 0.5f, 0.2f, 0f, 0f)
            EqualizerPreset.TREBLE_BOOST -> listOf(0f, 0f, 0.2f, 0.5f, 0.8f)
            EqualizerPreset.ROCK -> listOf(0.6f, 0.3f, -0.2f, 0.4f, 0.7f)
            EqualizerPreset.POP -> listOf(-0.2f, 0.3f, 0.6f, 0.4f, -0.2f)
            EqualizerPreset.CLASSICAL -> listOf(0.5f, 0.3f, -0.2f, 0.4f, 0.5f)
            EqualizerPreset.ELECTRONIC -> listOf(0.7f, 0.4f, 0f, 0.3f, 0.8f)
            EqualizerPreset.CUSTOM -> return
        }

        for (i in 0 until numBands) {
            val factor = if (i < gains.size) gains[i] else 0f
            val level = if (factor >= 0) {
                (factor * maxLevel).toInt().toShort()
            } else {
                ((-factor) * minLevel).toInt().toShort()
            }
            eq.setBandLevel(i.toShort(), level)
        }
    }

    fun getBands(): List<EqualizerBand> {
        val eq = equalizer ?: return emptyList()
        return try {
            val range = eq.bandLevelRange
            val minDb = range[0] / 100f
            val maxDb = range[1] / 100f

            (0 until eq.numberOfBands).map { bandIndex ->
                val centerFreq = eq.getCenterFreq(bandIndex.toShort()) / 1000
                val gainDb = eq.getBandLevel(bandIndex.toShort()) / 100f
                EqualizerBand(
                    index = bandIndex,
                    centerFreqHz = centerFreq,
                    gainDb = gainDb,
                    minDb = minDb,
                    maxDb = maxDb
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun release() {
        try {
            equalizer?.release()
            bassBoost?.release()
            virtualizer?.release()
        } catch (_: Exception) {}
        equalizer = null
        bassBoost = null
        virtualizer = null
        currentAudioSessionId = 0
    }
}
