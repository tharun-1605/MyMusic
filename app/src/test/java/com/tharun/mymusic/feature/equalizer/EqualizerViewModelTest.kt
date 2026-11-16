package com.tharun.mymusic.feature.equalizer

import com.tharun.mymusic.domain.model.EqualizerConfig
import com.tharun.mymusic.domain.model.EqualizerPreset
import com.tharun.mymusic.domain.repository.UserPreferencesRepository
import com.tharun.mymusic.service.AudioEffectController
import com.tharun.mymusic.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EqualizerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val preferencesRepo = mockk<UserPreferencesRepository>(relaxed = true)
    private val audioEffectController = mockk<AudioEffectController>(relaxed = true)
    private val configFlow = MutableStateFlow(EqualizerConfig())

    private lateinit var viewModel: EqualizerViewModel

    @Before
    fun setUp() {
        every { preferencesRepo.equalizerConfig } returns configFlow
        every { audioEffectController.getBands() } returns emptyList()
        viewModel = EqualizerViewModel(preferencesRepo, audioEffectController)
    }

    @Test
    fun `toggleEnabled updates state and persists`() = runTest {
        viewModel.toggleEnabled(true)

        assertTrue(viewModel.uiState.value.config.isEnabled)
        verify { audioEffectController.applyConfig(match { it.isEnabled }) }
        coVerify { preferencesRepo.saveEqualizerConfig(match { it.isEnabled }) }
    }

    @Test
    fun `selectPreset applies preset to controller and persists`() = runTest {
        viewModel.selectPreset(EqualizerPreset.BASS_BOOST)

        assertEquals(EqualizerPreset.BASS_BOOST, viewModel.uiState.value.config.preset)
        verify { audioEffectController.applyConfig(match { it.preset == EqualizerPreset.BASS_BOOST }) }
        coVerify { preferencesRepo.saveEqualizerConfig(match { it.preset == EqualizerPreset.BASS_BOOST }) }
    }

    @Test
    fun `setBassBoost and setVirtualizer apply levels`() = runTest {
        viewModel.setBassBoost(600)
        assertEquals(600, viewModel.uiState.value.config.bassBoostLevel)

        viewModel.setVirtualizer(800)
        assertEquals(800, viewModel.uiState.value.config.virtualizerLevel)
    }
}
