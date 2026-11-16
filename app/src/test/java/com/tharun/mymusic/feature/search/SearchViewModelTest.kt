package com.tharun.mymusic.feature.search

import app.cash.turbine.test
import com.tharun.mymusic.core.model.Resource
import com.tharun.mymusic.core.model.UiState
import com.tharun.mymusic.domain.model.SearchResult
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.FavoriteRepository
import com.tharun.mymusic.domain.repository.PlaybackRepository
import com.tharun.mymusic.domain.repository.SearchHistoryRepository
import com.tharun.mymusic.domain.usecase.SearchMusicUseCase
import com.tharun.mymusic.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val searchUseCase = mockk<SearchMusicUseCase>()
    private val searchHistoryRepo = mockk<SearchHistoryRepository>(relaxed = true)
    private val playbackRepo = mockk<PlaybackRepository>(relaxed = true)
    private val favoriteRepo = mockk<FavoriteRepository>(relaxed = true)

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        every { searchHistoryRepo.getRecentSearches(any()) } returns flowOf(listOf("Chopin", "Vivaldi"))
        viewModel = SearchViewModel(searchUseCase, searchHistoryRepo, playbackRepo, favoriteRepo)
    }

    @Test
    fun `initial recent searches are emitted`() = runTest {
        viewModel.recentSearches.test {
            val list = awaitItem()
            assertEquals(2, list.size)
            assertEquals("Chopin", list[0])
        }
    }

    @Test
    fun `onSearchSubmit triggers search and saves query`() = runTest {
        val result = SearchResult(
            songs = listOf(Song(id = "1", title = "Nocturne", artist = "Chopin")),
            query = "Nocturne"
        )
        coEvery { searchUseCase("Nocturne") } returns flowOf(Resource.Success(result))

        viewModel.onSearchSubmit("Nocturne")

        coVerify(exactly = 1) { searchHistoryRepo.saveSearchQuery("Nocturne") }

        viewModel.searchResultState.test {
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            assertEquals(1, (state as UiState.Success).data.songs.size)
        }
    }

    @Test
    fun `filter change updates selectedFilter state`() = runTest {
        viewModel.onFilterChange(SearchFilter.PLAYABLE_ONLY)
        assertEquals(SearchFilter.PLAYABLE_ONLY, viewModel.selectedFilter.value)

        viewModel.onFilterChange(SearchFilter.YOUTUBE_DISCOVERY)
        assertEquals(SearchFilter.YOUTUBE_DISCOVERY, viewModel.selectedFilter.value)
    }
}
