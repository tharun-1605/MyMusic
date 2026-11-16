package com.tharun.mymusic.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tharun.mymusic.domain.model.AudioSourceProvider
import com.tharun.mymusic.domain.model.Song

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val artworkUri: String?,
    val mediaUri: String?,
    val durationMs: Long,
    val isPlayable: Boolean,
    val provider: String,
    val youtubeVideoId: String?,
    val dateAdded: Long
) {
    fun toDomain(isFavorite: Boolean = false): Song = Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        artworkUri = artworkUri,
        mediaUri = mediaUri,
        durationMs = durationMs,
        isPlayable = isPlayable,
        provider = try { AudioSourceProvider.valueOf(provider) } catch (_: Exception) { AudioSourceProvider.SAMPLE },
        youtubeVideoId = youtubeVideoId,
        isFavorite = isFavorite,
        dateAdded = dateAdded
    )

    companion object {
        fun fromDomain(song: Song): SongEntity = SongEntity(
            id = song.id,
            title = song.title,
            artist = song.artist,
            album = song.album,
            artworkUri = song.artworkUri,
            mediaUri = song.mediaUri,
            durationMs = song.durationMs,
            isPlayable = song.isPlayable,
            provider = song.provider.name,
            youtubeVideoId = song.youtubeVideoId,
            dateAdded = song.dateAdded
        )
    }
}

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val description: String?,
    val artworkUri: String?,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "playlist_songs",
    primaryKeys = ["playlistId", "songId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["playlistId"]), Index(value = ["songId"])]
)
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: String,
    val sortOrder: Int
)

@Entity(
    tableName = "favorites",
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FavoriteEntity(
    @PrimaryKey val songId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "recently_played",
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecentlyPlayedEntity(
    @PrimaryKey val songId: String,
    val playedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val searchedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "lyrics",
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LyricsEntity(
    @PrimaryKey val songId: String,
    val plainText: String?,
    val lrcContent: String?,
    val cachedAt: Long = System.currentTimeMillis()
)
