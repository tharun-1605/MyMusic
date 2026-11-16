package com.tharun.mymusic.domain.usecase

import app.cash.turbine.test
import com.tharun.mymusic.core.model.Resource
import com.tharun.mymusic.domain.model.Playlist
import com.tharun.mymusic.domain.model.SearchResult
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.FavoriteRepository
import com.tharun.mymusic.domain.repository.LyricsRepository
import com.tharun.mymusic.domain.repository.MusicDiscoveryRepository
import com.tharun.mymusic.domain.repository.PlaylistRepository
import com.tharun.mymusic.domain.repository.RecentlyPlayedRepository
import com.tharun.mymusic.domain.repository.SongRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UseCasesTest {

    private val recentlyPlayedRepo = mockk<RecentlyPlayedRepository>()
    private val musicDiscoveryRepo = mockk<MusicDiscoveryRepository>()
    private val favoriteRepo = mockk<FavoriteRepository>()
    private val playlistRepo = mockk<PlaylistRepository>()
    private val songRepo = mockk<SongRepository>()
    private val lyricsRepo = mockk<LyricsRepository>()

    @Test
    fun `GetHomeDataUseCase combines all sources into HomeData`() = runTest {
        val testSongs = listOf(
            Song(id = "1", title = "Track 1", artist = "Artist 1")
        )
        val testPlaylists = listOf(
            Playlist(id = 1L, name = "Featured")
        )

        coEvery { recentlyPlayedRepo.getRecentlyPlayed(any()) } returns flowOf(testSongs)
        coEvery { musicDiscoveryRepo.getTrendingSongs() } returns flowOf(Resource.Success(testSongs))
        coEvery { musicDiscoveryRepo.getFeaturedPlaylists() } returns flowOf(Resource.Success(testPlaylists))
        coEvery { favoriteRepo.getFavoriteSongs() } returns flowOf(testSongs)

        val useCase = GetHomeDataUseCase(recentlyPlayedRepo, musicDiscoveryRepo, favoriteRepo)

        useCase().test {
            val item = awaitItem()
            assertTrue(item.greeting.isNotEmpty())
            assertEquals(1, item.recentlyPlayed.size)
            assertEquals(1, item.discoverSongs.size)
            assertEquals(1, item.featuredPlaylists.size)
            assertEquals(1, item.favoriteSongs.size)
            awaitComplete()
        }
    }

    @Test
    fun `SearchMusicUseCase delegates to MusicDiscoveryRepository`() = runTest {
        val searchResult = SearchResult(query = "debussy")
        coEvery { musicDiscoveryRepo.search("debussy") } returns flowOf(Resource.Success(searchResult))

        val useCase = SearchMusicUseCase(musicDiscoveryRepo, songRepo)

        useCase("debussy").test {
            val res = awaitItem()
            assertTrue(res is Resource.Success)
            assertEquals("debussy", (res as Resource.Success).data.query)
            awaitComplete()
        }
    }

    @Test
    fun `ToggleFavoriteUseCase calls repository toggleFavorite`() = runTest {
        coEvery { favoriteRepo.toggleFavorite("song_1") } returns Unit

        val useCase = ToggleFavoriteUseCase(favoriteRepo)
        useCase("song_1")

        coVerify(exactly = 1) { favoriteRepo.toggleFavorite("song_1") }
    }

    @Test
    fun `ManagePlaylistUseCase operations delegate correctly`() = runTest {
        coEvery { playlistRepo.createPlaylist("Chill", any()) } returns 42L
        coEvery { playlistRepo.renamePlaylist(42L, "Super Chill") } returns Unit
        coEvery { playlistRepo.deletePlaylist(42L) } returns Unit
        coEvery { playlistRepo.addSongToPlaylist(42L, "song_9") } returns Unit
        coEvery { playlistRepo.removeSongFromPlaylist(42L, "song_9") } returns Unit

        val useCase = ManagePlaylistUseCase(playlistRepo)

        assertEquals(42L, useCase.createPlaylist("Chill"))
        useCase.renamePlaylist(42L, "Super Chill")
        useCase.deletePlaylist(42L)
        useCase.addSongToPlaylist(42L, "song_9")
        useCase.removeSongFromPlaylist(42L, "song_9")

        coVerify(exactly = 1) { playlistRepo.createPlaylist("Chill", any()) }
        coVerify(exactly = 1) { playlistRepo.renamePlaylist(42L, "Super Chill") }
        coVerify(exactly = 1) { playlistRepo.deletePlaylist(42L) }
        coVerify(exactly = 1) { playlistRepo.addSongToPlaylist(42L, "song_9") }
        coVerify(exactly = 1) { playlistRepo.removeSongFromPlaylist(42L, "song_9") }
    }
}
