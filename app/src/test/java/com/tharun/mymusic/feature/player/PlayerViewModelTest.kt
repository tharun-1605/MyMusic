package com.tharun.mymusic.feature.player

import app.cash.turbine.test
import com.tharun.mymusic.domain.model.PlaybackState
import com.tharun.mymusic.domain.model.RepeatMode
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.FavoriteRepository
import com.tharun.mymusic.domain.repository.PlaybackRepository
import com.tharun.mymusic.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlayerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val playbackRepo = mockk<PlaybackRepository>(relaxed = true)
    private val favoriteRepo = mockk<FavoriteRepository>(relaxed = true)
    private val playbackStateFlow = MutableStateFlow(PlaybackState())

    private lateinit var viewModel: PlayerViewModel

    @Before
    fun setUp() {
        every { playbackRepo.playbackState } returns playbackStateFlow
        viewModel = PlayerViewModel(playbackRepo, favoriteRepo)
    }

    @Test
    fun `togglePlayPause resumes when paused and pauses when playing`() {
        playbackStateFlow.value = PlaybackState(isPlaying = false)
        viewModel.togglePlayPause()
        verify(exactly = 1) { playbackRepo.resume() }

        playbackStateFlow.value = PlaybackState(isPlaying = true)
        viewModel.togglePlayPause()
        verify(exactly = 1) { playbackRepo.pause() }
    }

    @Test
    fun `next and previous call playback repository`() {
        viewModel.next()
        verify(exactly = 1) { playbackRepo.next() }

        viewModel.previous()
        verify(exactly = 1) { playbackRepo.previous() }
    }

    @Test
    fun `toggleRepeat cycles through OFF, ALL, ONE`() {
        playbackStateFlow.value = PlaybackState(repeatMode = RepeatMode.OFF)
        viewModel.toggleRepeat()
        verify { playbackRepo.setRepeatMode(RepeatMode.ALL) }

        playbackStateFlow.value = PlaybackState(repeatMode = RepeatMode.ALL)
        viewModel.toggleRepeat()
        verify { playbackRepo.setRepeatMode(RepeatMode.ONE) }

        playbackStateFlow.value = PlaybackState(repeatMode = RepeatMode.ONE)
        viewModel.toggleRepeat()
        verify { playbackRepo.setRepeatMode(RepeatMode.OFF) }
    }

    @Test
    fun `toggleFavorite delegates to FavoriteRepository`() = runTest {
        val testSong = Song(id = "fav_1", title = "Favorite Track", artist = "Artist")
        viewModel.toggleFavorite(testSong)
        coVerify(exactly = 1) { favoriteRepo.toggleFavorite("fav_1") }
    }
}
