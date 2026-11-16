package com.tharun.mymusic.domain.usecase

import com.tharun.mymusic.core.model.Resource
import com.tharun.mymusic.domain.model.Lyrics
import com.tharun.mymusic.domain.model.Playlist
import com.tharun.mymusic.domain.model.SearchResult
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.FavoriteRepository
import com.tharun.mymusic.domain.repository.LyricsRepository
import com.tharun.mymusic.domain.repository.MusicDiscoveryRepository
import com.tharun.mymusic.domain.repository.PlaylistRepository
import com.tharun.mymusic.domain.repository.RecentlyPlayedRepository
import com.tharun.mymusic.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class HomeData(
    val greeting: String,
    val recentlyPlayed: List<Song>,
    val discoverSongs: List<Song>,
    val featuredPlaylists: List<Playlist>,
    val favoriteSongs: List<Song>
)

class GetHomeDataUseCase @Inject constructor(
    private val recentlyPlayedRepository: RecentlyPlayedRepository,
    private val musicDiscoveryRepository: MusicDiscoveryRepository,
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): Flow<HomeData> {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 4..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Late night vibes"
        }

        return combine(
            recentlyPlayedRepository.getRecentlyPlayed(15),
            musicDiscoveryRepository.getTrendingSongs(),
            musicDiscoveryRepository.getFeaturedPlaylists(),
            favoriteRepository.getFavoriteSongs()
        ) { recent, trendingRes, playlistsRes, favorites ->
            val trending = if (trendingRes is Resource.Success) trendingRes.data else emptyList()
            val playlists = if (playlistsRes is Resource.Success) playlistsRes.data else emptyList()
            HomeData(
                greeting = greeting,
                recentlyPlayed = recent,
                discoverSongs = trending,
                featuredPlaylists = playlists,
                favoriteSongs = favorites
            )
        }
    }
}

class SearchMusicUseCase @Inject constructor(
    private val discoveryRepository: MusicDiscoveryRepository,
    private val songRepository: SongRepository
) {
    operator fun invoke(query: String): Flow<Resource<SearchResult>> {
        return discoveryRepository.search(query)
    }
}

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(songId: String) {
        favoriteRepository.toggleFavorite(songId)
    }
}

class ManagePlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend fun createPlaylist(name: String, description: String? = null): Long =
        playlistRepository.createPlaylist(name, description)

    suspend fun renamePlaylist(playlistId: Long, newName: String) =
        playlistRepository.renamePlaylist(playlistId, newName)

    suspend fun deletePlaylist(playlistId: Long) =
        playlistRepository.deletePlaylist(playlistId)

    suspend fun addSongToPlaylist(playlistId: Long, songId: String) =
        playlistRepository.addSongToPlaylist(playlistId, songId)

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String) =
        playlistRepository.removeSongFromPlaylist(playlistId, songId)

    suspend fun reorderSongs(playlistId: Long, songIds: List<String>) =
        playlistRepository.reorderSongsInPlaylist(playlistId, songIds)
}

class GetLyricsUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository
) {
    operator fun invoke(songId: String): Flow<Lyrics?> =
        lyricsRepository.getLyrics(songId)

    suspend fun saveLyrics(lyrics: Lyrics) =
        lyricsRepository.saveLyrics(lyrics)
}

class ScanDeviceAudioUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(): List<Song> =
        songRepository.scanLocalMedia()
}
