package com.tharun.mymusic.data.repository

import com.tharun.mymusic.core.dispatchers.DispatcherProvider
import com.tharun.mymusic.core.model.Resource
import com.tharun.mymusic.core.theme.ThemeMode
import com.tharun.mymusic.core.util.LrcParser
import com.tharun.mymusic.data.local.dao.FavoriteDao
import com.tharun.mymusic.data.local.dao.LyricsDao
import com.tharun.mymusic.data.local.dao.PlaylistDao
import com.tharun.mymusic.data.local.dao.RecentlyPlayedDao
import com.tharun.mymusic.data.local.dao.SearchHistoryDao
import com.tharun.mymusic.data.local.dao.SongDao
import com.tharun.mymusic.data.local.entity.FavoriteEntity
import com.tharun.mymusic.data.local.entity.LyricsEntity
import com.tharun.mymusic.data.local.entity.PlaylistEntity
import com.tharun.mymusic.data.local.entity.PlaylistSongCrossRef
import com.tharun.mymusic.data.local.entity.RecentlyPlayedEntity
import com.tharun.mymusic.data.local.entity.SearchHistoryEntity
import com.tharun.mymusic.data.local.entity.SongEntity
import com.tharun.mymusic.data.local.preferences.UserPreferencesDataStore
import com.tharun.mymusic.data.remote.discovery.LocalDeviceDiscoveryProvider
import com.tharun.mymusic.data.remote.discovery.SampleDiscoveryProvider
import com.tharun.mymusic.data.remote.discovery.YouTubeDiscoveryProvider
import com.tharun.mymusic.data.remote.sample.SamplePermittedCatalog
import com.tharun.mymusic.domain.model.EqualizerConfig
import com.tharun.mymusic.domain.model.Lyrics
import com.tharun.mymusic.domain.model.Playlist
import com.tharun.mymusic.domain.model.SearchResult
import com.tharun.mymusic.domain.model.Song
import com.tharun.mymusic.domain.repository.FavoriteRepository
import com.tharun.mymusic.domain.repository.LyricsRepository
import com.tharun.mymusic.domain.repository.MusicDiscoveryRepository
import com.tharun.mymusic.domain.repository.PlaylistRepository
import com.tharun.mymusic.domain.repository.RecentlyPlayedRepository
import com.tharun.mymusic.domain.repository.SearchHistoryRepository
import com.tharun.mymusic.domain.repository.SongRepository
import com.tharun.mymusic.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepositoryImpl @Inject constructor(
    private val songDao: SongDao,
    private val favoriteDao: FavoriteDao,
    private val localDeviceDiscoveryProvider: LocalDeviceDiscoveryProvider,
    private val dispatchers: DispatcherProvider
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> {
        return combine(songDao.getAllSongs(), favoriteDao.getFavoriteSongs()) { entities, favorites ->
            val favIds = favorites.map { it.id }.toSet()
            entities.map { it.toDomain(isFavorite = favIds.contains(it.id)) }
        }.flowOn(dispatchers.io)
    }

    override fun getSongById(id: String): Flow<Song?> {
        return combine(songDao.getSongById(id), favoriteDao.isFavorite(id)) { entity, isFav ->
            entity?.toDomain(isFavorite = isFav)
        }.flowOn(dispatchers.io)
    }

    override suspend fun insertSong(song: Song) = withContext(dispatchers.io) {
        songDao.insertSong(SongEntity.fromDomain(song))
    }

    override suspend fun insertSongs(songs: List<Song>) = withContext(dispatchers.io) {
        songDao.insertSongs(songs.map { SongEntity.fromDomain(it) })
    }

    override suspend fun searchLocal(query: String): List<Song> = withContext(dispatchers.io) {
        val entities = songDao.searchSongs(query)
        entities.map {
            val isFav = favoriteDao.isFavoriteSync(it.id)
            it.toDomain(isFavorite = isFav)
        }
    }

    override suspend fun scanLocalMedia(): List<Song> = withContext(dispatchers.io) {
        val scanned = localDeviceDiscoveryProvider.scanDeviceAudio()
        if (scanned.isNotEmpty()) {
            insertSongs(scanned)
        }
        scanned
    }

    suspend fun seedSampleCatalogIfEmpty() = withContext(dispatchers.io) {
        val current = songDao.searchSongs("")
        if (current.isEmpty()) {
            insertSongs(SamplePermittedCatalog.sampleSongs)
        }
    }
}

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val songDao: SongDao,
    private val favoriteDao: FavoriteDao,
    private val dispatchers: DispatcherProvider
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { list ->
            list.map { entity ->
                Playlist(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    artworkUri = entity.artworkUri,
                    createdAt = entity.createdAt
                )
            }
        }.flowOn(dispatchers.io)
    }

    override fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistById(playlistId).map { entity ->
            entity?.let {
                Playlist(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    artworkUri = it.artworkUri,
                    createdAt = it.createdAt
                )
            }
        }.flowOn(dispatchers.io)
    }

    override fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> {
        return combine(
            playlistDao.getSongsForPlaylist(playlistId),
            favoriteDao.getFavoriteSongs()
        ) { songs, favorites ->
            val favIds = favorites.map { it.id }.toSet()
            songs.map { it.toDomain(isFavorite = favIds.contains(it.id)) }
        }.flowOn(dispatchers.io)
    }

    override suspend fun createPlaylist(name: String, description: String?): Long =
        withContext(dispatchers.io) {
            playlistDao.insertPlaylist(
                PlaylistEntity(
                    name = name,
                    description = description,
                    artworkUri = null
                )
            )
        }

    override suspend fun renamePlaylist(playlistId: Long, newName: String) =
        withContext(dispatchers.io) {
            playlistDao.renamePlaylist(playlistId, newName)
        }

    override suspend fun deletePlaylist(playlistId: Long) = withContext(dispatchers.io) {
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun addSongToPlaylist(playlistId: Long, songId: String) =
        withContext(dispatchers.io) {
            playlistDao.insertPlaylistSong(
                PlaylistSongCrossRef(
                    playlistId = playlistId,
                    songId = songId,
                    sortOrder = System.currentTimeMillis().toInt()
                )
            )
        }

    override suspend fun removeSongFromPlaylist(playlistId: Long, songId: String) =
        withContext(dispatchers.io) {
            playlistDao.removeSongFromPlaylist(playlistId, songId)
        }

    override suspend fun reorderSongsInPlaylist(playlistId: Long, songIds: List<String>) =
        withContext(dispatchers.io) {
            playlistDao.reorderSongs(playlistId, songIds)
        }

    suspend fun seedSamplePlaylistsIfEmpty() = withContext(dispatchers.io) {
        val sampleSongs = SamplePermittedCatalog.sampleSongs
        for (samplePl in SamplePermittedCatalog.samplePlaylists) {
            val id = playlistDao.insertPlaylist(
                PlaylistEntity(
                    name = samplePl.name,
                    description = samplePl.description,
                    artworkUri = samplePl.artworkUri
                )
            )
            sampleSongs.take(samplePl.songCount).forEachIndexed { index, song ->
                playlistDao.insertPlaylistSong(PlaylistSongCrossRef(id, song.id, index))
            }
        }
    }
}

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val dispatchers: DispatcherProvider
) : FavoriteRepository {

    override fun getFavoriteSongs(): Flow<List<Song>> {
        return favoriteDao.getFavoriteSongs().map { entities ->
            entities.map { it.toDomain(isFavorite = true) }
        }.flowOn(dispatchers.io)
    }

    override fun isFavorite(songId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(songId).flowOn(dispatchers.io)
    }

    override suspend fun toggleFavorite(songId: String) = withContext(dispatchers.io) {
        val exists = favoriteDao.isFavoriteSync(songId)
        if (exists) {
            favoriteDao.deleteFavorite(songId)
        } else {
            favoriteDao.insertFavorite(FavoriteEntity(songId = songId))
        }
    }
}

@Singleton
class RecentlyPlayedRepositoryImpl @Inject constructor(
    private val recentlyPlayedDao: RecentlyPlayedDao,
    private val favoriteDao: FavoriteDao,
    private val dispatchers: DispatcherProvider
) : RecentlyPlayedRepository {

    override fun getRecentlyPlayed(limit: Int): Flow<List<Song>> {
        return combine(
            recentlyPlayedDao.getRecentlyPlayed(limit),
            favoriteDao.getFavoriteSongs()
        ) { songs, favorites ->
            val favIds = favorites.map { it.id }.toSet()
            songs.map { it.toDomain(isFavorite = favIds.contains(it.id)) }
        }.flowOn(dispatchers.io)
    }

    override suspend fun addRecentlyPlayed(songId: String) = withContext(dispatchers.io) {
        recentlyPlayedDao.insertRecentlyPlayed(RecentlyPlayedEntity(songId = songId))
    }

    override suspend fun clearHistory() = withContext(dispatchers.io) {
        recentlyPlayedDao.clearAll()
    }
}

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao,
    private val dispatchers: DispatcherProvider
) : SearchHistoryRepository {

    override fun getRecentSearches(limit: Int): Flow<List<String>> =
        searchHistoryDao.getRecentSearches(limit).flowOn(dispatchers.io)

    override suspend fun saveSearchQuery(query: String) = withContext(dispatchers.io) {
        if (query.isNotBlank()) {
            searchHistoryDao.insertSearch(SearchHistoryEntity(query.trim()))
        }
    }

    override suspend fun deleteSearchQuery(query: String) = withContext(dispatchers.io) {
        searchHistoryDao.deleteSearch(query)
    }

    override suspend fun clearSearchHistory() = withContext(dispatchers.io) {
        searchHistoryDao.clearAll()
    }
}

@Singleton
class LyricsRepositoryImpl @Inject constructor(
    private val lyricsDao: LyricsDao,
    private val dispatchers: DispatcherProvider
) : LyricsRepository {

    override fun getLyrics(songId: String): Flow<Lyrics?> {
        return lyricsDao.getLyrics(songId).map { entity ->
            if (entity != null) {
                LrcParser.parse(songId, entity.lrcContent ?: entity.plainText)
            } else {
                // Check sample lyrics fallback
                val sampleLrc = SamplePermittedCatalog.sampleLrcLyrics[songId]
                if (sampleLrc != null) {
                    LrcParser.parse(songId, sampleLrc)
                } else {
                    null
                }
            }
        }.flowOn(dispatchers.io)
    }

    override suspend fun saveLyrics(lyrics: Lyrics) = withContext(dispatchers.io) {
        lyricsDao.insertLyrics(
            LyricsEntity(
                songId = lyrics.songId,
                plainText = lyrics.plainText,
                lrcContent = lyrics.plainText
            )
        )
    }
}

@Singleton
class MusicDiscoveryRepositoryImpl @Inject constructor(
    private val sampleDiscoveryProvider: SampleDiscoveryProvider,
    private val youTubeDiscoveryProvider: YouTubeDiscoveryProvider,
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository,
    private val recentlyPlayedDao: RecentlyPlayedDao,
    private val dispatchers: DispatcherProvider
) : MusicDiscoveryRepository {

    override fun search(query: String): Flow<Resource<SearchResult>> = flow {
        emit(Resource.Loading)
        try {
            val q = query.trim()
            if (q.isEmpty()) {
                emit(Resource.Success(SearchResult(query = q)))
                return@flow
            }

            // 1. Search YouTube first (InnerTube / SimpMusic architecture - Songs only)
            val ytResults = youTubeDiscoveryProvider.search(q)

            // 2. Search local database
            val localSongs = songRepository.searchLocal(q)

            // 3. Search sample catalog
            val sampleResults = sampleDiscoveryProvider.search(q)

            val combinedSongs = (ytResults.songs + localSongs + sampleResults.songs).distinctBy { it.id }
            val combinedArtists = (ytResults.artists + sampleResults.artists).distinctBy { it.name }
            val combinedAlbums = (ytResults.albums + sampleResults.albums).distinctBy { it.title }

            emit(
                Resource.Success(
                    SearchResult(
                        songs = combinedSongs,
                        artists = combinedArtists,
                        albums = combinedAlbums,
                        query = q
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to search music", e))
        }
    }.flowOn(dispatchers.io)

    override fun getTrendingSongs(): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading)
        try {
            // Check if user has recently played songs to personalize recommendations based on their listening
            val recent: List<SongEntity> = try {
                recentlyPlayedDao.getRecentlyPlayed(5).first()
            } catch (_: Exception) {
                emptyList()
            }

            if (recent.isNotEmpty()) {
                val seed = recent.firstOrNull { it.id.startsWith("yt_") || it.youtubeVideoId != null }
                val vId = seed?.youtubeVideoId ?: seed?.id?.removePrefix("yt_")
                if (!vId.isNullOrBlank()) {
                    val related = youTubeDiscoveryProvider.getRelated(vId)
                    if (related.isNotEmpty()) {
                        emit(Resource.Success(related))
                        return@flow
                    }
                }
                val artist = recent.first().artist
                if (artist.isNotBlank() && artist != "Unknown Artist") {
                    val artistResults = youTubeDiscoveryProvider.search(artist)
                    if (artistResults.songs.isNotEmpty()) {
                        emit(Resource.Success(artistResults.songs))
                        return@flow
                    }
                }
            }

            val ytTrending = youTubeDiscoveryProvider.getTrending()
            val sampleTrending = sampleDiscoveryProvider.getTrending()
            val combined = if (ytTrending.isNotEmpty()) {
                (ytTrending + sampleTrending).distinctBy { it.id }
            } else {
                sampleTrending
            }
            emit(Resource.Success(combined))
        } catch (e: Exception) {
            emit(Resource.Success(sampleDiscoveryProvider.getTrending()))
        }
    }.flowOn(dispatchers.io)

    override fun getRelatedSongs(videoId: String): Flow<List<Song>> = flow {
        try {
            val related = youTubeDiscoveryProvider.getRelated(videoId)
            emit(related)
        } catch (_: Exception) {
            emit(emptyList())
        }
    }.flowOn(dispatchers.io)

    override fun getFeaturedPlaylists(): Flow<Resource<List<Playlist>>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(SamplePermittedCatalog.samplePlaylists))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load featured playlists", e))
        }
    }.flowOn(dispatchers.io)
}

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val preferencesDataStore: UserPreferencesDataStore
) : UserPreferencesRepository {

    override val themeMode: Flow<ThemeMode> = preferencesDataStore.themeMode

    override suspend fun setThemeMode(mode: ThemeMode) {
        preferencesDataStore.setThemeMode(mode)
    }

    override val equalizerConfig: Flow<EqualizerConfig> = preferencesDataStore.equalizerConfig

    override suspend fun saveEqualizerConfig(config: EqualizerConfig) {
        preferencesDataStore.saveEqualizerConfig(config)
    }

    override val youtubeApiKey: Flow<String> = preferencesDataStore.youtubeApiKey

    override suspend fun setYoutubeApiKey(key: String) {
        preferencesDataStore.setYoutubeApiKey(key)
    }
}
