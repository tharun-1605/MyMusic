package com.tharun.mymusic.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tharun.mymusic.data.local.entity.FavoriteEntity
import com.tharun.mymusic.data.local.entity.LyricsEntity
import com.tharun.mymusic.data.local.entity.PlaylistEntity
import com.tharun.mymusic.data.local.entity.PlaylistSongCrossRef
import com.tharun.mymusic.data.local.entity.RecentlyPlayedEntity
import com.tharun.mymusic.data.local.entity.SearchHistoryEntity
import com.tharun.mymusic.data.local.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY title ASC")
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id")
    fun getSongById(id: String): Flow<SongEntity?>

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongByIdSync(id: String): SongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'")
    suspend fun searchSongs(query: String): List<SongEntity>
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    fun getPlaylistById(id: Long): Flow<PlaylistEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("UPDATE playlists SET name = :newName WHERE id = :playlistId")
    suspend fun renamePlaylist(playlistId: Long, newName: String)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN playlist_songs ps ON s.id = ps.songId
        WHERE ps.playlistId = :playlistId
        ORDER BY ps.sortOrder ASC
    """)
    fun getSongsForPlaylist(playlistId: Long): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSong(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun clearPlaylistSongs(playlistId: Long)

    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    fun getPlaylistSongCount(playlistId: Long): Flow<Int>

    @Transaction
    suspend fun reorderSongs(playlistId: Long, songIds: List<String>) {
        clearPlaylistSongs(playlistId)
        songIds.forEachIndexed { index, songId ->
            insertPlaylistSong(PlaylistSongCrossRef(playlistId, songId, index))
        }
    }
}

@Dao
interface FavoriteDao {
    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN favorites f ON s.id = f.songId
        ORDER BY f.addedAt DESC
    """)
    fun getFavoriteSongs(): Flow<List<SongEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE songId = :songId)")
    fun isFavorite(songId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE songId = :songId)")
    suspend fun isFavoriteSync(songId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE songId = :songId")
    suspend fun deleteFavorite(songId: String)
}

@Dao
interface RecentlyPlayedDao {
    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN recently_played rp ON s.id = rp.songId
        ORDER BY rp.playedAt DESC
        LIMIT :limit
    """)
    fun getRecentlyPlayed(limit: Int): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentlyPlayed(item: RecentlyPlayedEntity)

    @Query("DELETE FROM recently_played")
    suspend fun clearAll()
}

@Dao
interface SearchHistoryDao {
    @Query("SELECT `query` FROM search_history ORDER BY searchedAt DESC LIMIT :limit")
    fun getRecentSearches(limit: Int): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(item: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE `query` = :query")
    suspend fun deleteSearch(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAll()
}

@Dao
interface LyricsDao {
    @Query("SELECT * FROM lyrics WHERE songId = :songId")
    fun getLyrics(songId: String): Flow<LyricsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLyrics(lyrics: LyricsEntity)
}
