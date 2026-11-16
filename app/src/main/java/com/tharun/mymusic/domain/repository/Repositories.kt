package com.tharun.mymusic.domain.repository

import com.tharun.mymusic.core.model.Resource
import com.tharun.mymusic.core.theme.ThemeMode
import com.tharun.mymusic.domain.model.Album
import com.tharun.mymusic.domain.model.Artist
import com.tharun.mymusic.domain.model.EqualizerConfig
import com.tharun.mymusic.domain.model.Lyrics
import com.tharun.mymusic.domain.model.PlaybackState
import com.tharun.mymusic.domain.model.Playlist
import com.tharun.mymusic.domain.model.RepeatMode
import com.tharun.mymusic.domain.model.SearchResult
import com.tharun.mymusic.domain.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>
    fun getSongById(id: String): Flow<Song?>
    suspend fun insertSong(song: Song)
    suspend fun insertSongs(songs: List<Song>)
    suspend fun searchLocal(query: String): List<Song>
    suspend fun scanLocalMedia(): List<Song>
}

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(playlistId: Long): Flow<Playlist?>
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>>
    suspend fun createPlaylist(name: String, description: String? = null): Long
    suspend fun renamePlaylist(playlistId: Long, newName: String)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addSongToPlaylist(playlistId: Long, songId: String)
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String)
    suspend fun reorderSongsInPlaylist(playlistId: Long, songIds: List<String>)
}

interface FavoriteRepository {
    fun getFavoriteSongs(): Flow<List<Song>>
    fun isFavorite(songId: String): Flow<Boolean>
    suspend fun toggleFavorite(songId: String)
}

interface RecentlyPlayedRepository {
    fun getRecentlyPlayed(limit: Int = 20): Flow<List<Song>>
    suspend fun addRecentlyPlayed(songId: String)
    suspend fun clearHistory()
}

interface SearchHistoryRepository {
    fun getRecentSearches(limit: Int = 10): Flow<List<String>>
    suspend fun saveSearchQuery(query: String)
    suspend fun deleteSearchQuery(query: String)
    suspend fun clearSearchHistory()
}

interface LyricsRepository {
    fun getLyrics(songId: String): Flow<Lyrics?>
    suspend fun saveLyrics(lyrics: Lyrics)
}

interface MusicDiscoveryRepository {
    fun search(query: String): Flow<Resource<SearchResult>>
    fun getTrendingSongs(): Flow<Resource<List<Song>>>
    fun getFeaturedPlaylists(): Flow<Resource<List<Playlist>>>
}

interface PlaybackRepository {
    val playbackState: StateFlow<PlaybackState>
    fun playSong(song: Song)
    fun playQueue(songs: List<Song>, startIndex: Int = 0)
    fun resume()
    fun pause()
    fun next()
    fun previous()
    fun seekTo(positionMs: Long)
    fun setRepeatMode(mode: RepeatMode)
    fun toggleShuffle()
    fun removeFromQueue(index: Int)
    fun reorderQueue(fromIndex: Int, toIndex: Int)
    fun clearQueue()
    fun getAudioSessionId(): Int
}

interface UserPreferencesRepository {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
    val equalizerConfig: Flow<EqualizerConfig>
    suspend fun saveEqualizerConfig(config: EqualizerConfig)
    val youtubeApiKey: Flow<String>
    suspend fun setYoutubeApiKey(key: String)
}
