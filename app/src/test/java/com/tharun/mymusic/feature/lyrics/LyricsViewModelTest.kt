package com.tharun.mymusic.feature.lyrics

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.tharun.mymusic.domain.model.Lyrics
import com.tharun.mymusic.domain.model.LyricsLine
import com.tharun.mymusic.domain.model.PlaybackState
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.PlaybackRepository
import com.tharun.mymusic.domain.repository.SongRepository
import com.tharun.mymusic.domain.usecase.GetLyricsUseCase
import com.tharun.mymusic.testutil.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LyricsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getLyricsUseCase = mockk<GetLyricsUseCase>()
    private val songRepository = mockk<SongRepository>()
    private val playbackRepository = mockk<PlaybackRepository>(relaxed = true)
    private val playbackStateFlow = MutableStateFlow(PlaybackState())
    private val savedStateHandle = SavedStateHandle(mapOf("songId" to "test_1"))

    private lateinit var viewModel: LyricsViewModel

    @Before
    fun setUp() {
        val testLyrics = Lyrics(
            songId = "test_1",
            plainText = "Line 1\nLine 2",
            lines = listOf(
                LyricsLine(timestampMs = 5000L, text = "Line 1"),
                LyricsLine(timestampMs = 15000L, text = "Line 2")
            ),
            isSynced = true
        )

        every { getLyricsUseCase("test_1") } returns flowOf(testLyrics)
        every { songRepository.getSongById("test_1") } returns flowOf(Song(id = "test_1", title = "Song", artist = "Artist"))
        every { playbackRepository.playbackState } returns playbackStateFlow

        viewModel = LyricsViewModel(getLyricsUseCase, songRepository, playbackRepository, savedStateHandle)
    }

    @Test
    fun `lyrics line is synced with current playback position`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        playbackStateFlow.value = PlaybackState(currentPositionMs = 6000L)
        assertEquals(0, viewModel.uiState.value.activeLineIndex)

        playbackStateFlow.value = PlaybackState(currentPositionMs = 16000L)
        assertEquals(1, viewModel.uiState.value.activeLineIndex)
    }

    @Test
    fun `user scroll pauses auto-scroll and resume restores it`() {
        viewModel.onUserScrolled()
        assertTrue(viewModel.isAutoScrollPaused.value)

        viewModel.resumeAutoScroll()
        assertFalse(viewModel.isAutoScrollPaused.value)
    }

    @Test
    fun `seekToLine calls playbackRepository seekTo and resumes scroll`() {
        viewModel.seekToLine(15000L)
        verify { playbackRepository.seekTo(15000L) }
        assertFalse(viewModel.isAutoScrollPaused.value)
    }
}
