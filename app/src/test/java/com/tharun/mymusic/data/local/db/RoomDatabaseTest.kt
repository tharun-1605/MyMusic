package com.tharun.mymusic.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class RoomDatabaseTest {

    private lateinit var database: MyMusicDatabase
    private lateinit var songDao: SongDao
    private lateinit var playlistDao: PlaylistDao
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var recentlyPlayedDao: RecentlyPlayedDao
    private lateinit var searchHistoryDao: SearchHistoryDao
    private lateinit var lyricsDao: LyricsDao

    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MyMusicDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        songDao = database.songDao()
        playlistDao = database.playlistDao()
        favoriteDao = database.favoriteDao()
        recentlyPlayedDao = database.recentlyPlayedDao()
        searchHistoryDao = database.searchHistoryDao()
        lyricsDao = database.lyricsDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndQuerySong() = runTest {
        val song = SongEntity(
            id = "s_1",
            title = "Clair de Lune",
            artist = "Claude Debussy",
            album = "Suite Bergamasque",
            artworkUri = null,
            mediaUri = "https://example.com/audio.ogg",
            durationMs = 300000L,
            isPlayable = true,
            provider = "SAMPLE",
            youtubeVideoId = null,
            dateAdded = System.currentTimeMillis()
        )

        songDao.insertSong(song)
        val loaded = songDao.getSongByIdSync("s_1")

        assertNotNull(loaded)
        assertEquals("Clair de Lune", loaded?.title)
        assertEquals("Claude Debussy", loaded?.artist)

        val searched = songDao.searchSongs("debussy")
        assertEquals(1, searched.size)
        assertEquals("s_1", searched[0].id)
    }

    @Test
    fun favoriteOperations() = runTest {
        val song = SongEntity(
            id = "s_fav",
            title = "Nocturne",
            artist = "Chopin",
            album = "Op. 9",
            artworkUri = null,
            mediaUri = "https://example.com/chopin.ogg",
            durationMs = 260000L,
            isPlayable = true,
            provider = "SAMPLE",
            youtubeVideoId = null,
            dateAdded = System.currentTimeMillis()
        )
        songDao.insertSong(song)

        assertFalse(favoriteDao.isFavoriteSync("s_fav"))

        favoriteDao.insertFavorite(FavoriteEntity("s_fav"))
        assertTrue(favoriteDao.isFavoriteSync("s_fav"))

        val favorites = favoriteDao.getFavoriteSongs().first()
        assertEquals(1, favorites.size)
        assertEquals("Nocturne", favorites[0].title)

        favoriteDao.deleteFavorite("s_fav")
        assertFalse(favoriteDao.isFavoriteSync("s_fav"))
    }

    @Test
    fun playlistOperations() = runTest {
        val song1 = SongEntity(
            id = "s_p1",
            title = "Track 1",
            artist = "Artist",
            album = "Album",
            artworkUri = null,
            mediaUri = "https://example.com/1.ogg",
            durationMs = 180000L,
            isPlayable = true,
            provider = "SAMPLE",
            youtubeVideoId = null,
            dateAdded = System.currentTimeMillis()
        )
        val song2 = SongEntity(
            id = "s_p2",
            title = "Track 2",
            artist = "Artist",
            album = "Album",
            artworkUri = null,
            mediaUri = "https://example.com/2.ogg",
            durationMs = 210000L,
            isPlayable = true,
            provider = "SAMPLE",
            youtubeVideoId = null,
            dateAdded = System.currentTimeMillis()
        )
        songDao.insertSongs(listOf(song1, song2))

        val playlistId = playlistDao.insertPlaylist(
            PlaylistEntity(name = "My Classical Playlist", description = "Peaceful melodies", artworkUri = null)
        )

        playlistDao.insertPlaylistSong(PlaylistSongCrossRef(playlistId, "s_p1", 0))
        playlistDao.insertPlaylistSong(PlaylistSongCrossRef(playlistId, "s_p2", 1))

        val songs = playlistDao.getSongsForPlaylist(playlistId).first()
        assertEquals(2, songs.size)
        assertEquals("Track 1", songs[0].title)
        assertEquals("Track 2", songs[1].title)

        playlistDao.reorderSongs(playlistId, listOf("s_p2", "s_p1"))
        val reordered = playlistDao.getSongsForPlaylist(playlistId).first()
        assertEquals("Track 2", reordered[0].title)
        assertEquals("Track 1", reordered[1].title)

        playlistDao.removeSongFromPlaylist(playlistId, "s_p2")
        val afterRemove = playlistDao.getSongsForPlaylist(playlistId).first()
        assertEquals(1, afterRemove.size)

        playlistDao.deletePlaylist(playlistId)
        val playlistAfterDelete = playlistDao.getPlaylistById(playlistId).first()
        assertNull(playlistAfterDelete)
    }

    @Test
    fun recentlyPlayedAndHistory() = runTest {
        val song = SongEntity(
            id = "s_rec",
            title = "Vivaldi Spring",
            artist = "Vivaldi",
            album = "Four Seasons",
            artworkUri = null,
            mediaUri = "https://example.com/spring.ogg",
            durationMs = 216000L,
            isPlayable = true,
            provider = "SAMPLE",
            youtubeVideoId = null,
            dateAdded = System.currentTimeMillis()
        )
        songDao.insertSong(song)

        recentlyPlayedDao.insertRecentlyPlayed(RecentlyPlayedEntity("s_rec"))
        val recents = recentlyPlayedDao.getRecentlyPlayed(10).first()
        assertEquals(1, recents.size)

        recentlyPlayedDao.clearAll()
        val emptyRecents = recentlyPlayedDao.getRecentlyPlayed(10).first()
        assertTrue(emptyRecents.isEmpty())

        searchHistoryDao.insertSearch(SearchHistoryEntity("Beethoven"))
        val history = searchHistoryDao.getRecentSearches(10).first()
        assertEquals(1, history.size)
        assertEquals("Beethoven", history[0])
    }

    @Test
    fun lyricsCaching() = runTest {
        val song = SongEntity(
            id = "s_lyr",
            title = "Gymnopédie",
            artist = "Erik Satie",
            album = "Piano",
            artworkUri = null,
            mediaUri = "https://example.com/satie.ogg",
            durationMs = 200000L,
            isPlayable = true,
            provider = "SAMPLE",
            youtubeVideoId = null,
            dateAdded = System.currentTimeMillis()
        )
        songDao.insertSong(song)

        val lrc = "[00:10.00]Gentle piano chords"
        lyricsDao.insertLyrics(
            LyricsEntity(
                songId = "s_lyr",
                plainText = "Gentle piano chords",
                lrcContent = lrc
            )
        )

        val cached = lyricsDao.getLyrics("s_lyr").first()
        assertNotNull(cached)
        assertEquals(lrc, cached?.lrcContent)
    }
}
