package com.tharun.mymusic.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        SongEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        FavoriteEntity::class,
        RecentlyPlayedEntity::class,
        SearchHistoryEntity::class,
        LyricsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MyMusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentlyPlayedDao(): RecentlyPlayedDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun lyricsDao(): LyricsDao

    companion object {
        const val DATABASE_NAME = "mymusic_database.db"
    }
}
