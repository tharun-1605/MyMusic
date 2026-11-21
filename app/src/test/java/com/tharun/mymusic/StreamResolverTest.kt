package com.tharun.mymusic

import com.tharun.mymusic.core.dispatchers.DispatcherProvider
import com.tharun.mymusic.data.remote.stream.OkHttpDownloader
import com.tharun.mymusic.data.remote.stream.YouTubeStreamResolver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StreamResolverTest {

    private val testDispatchers = object : DispatcherProvider {
        override val main: CoroutineDispatcher = Dispatchers.Unconfined
        override val io: CoroutineDispatcher = Dispatchers.IO
        override val default: CoroutineDispatcher = Dispatchers.Default
        override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
    }

    @Test
    fun testResolveAudioStreamNeverNull() = runBlocking {
        val client = OkHttpClient()
        val downloader = OkHttpDownloader(client)
        val resolver = YouTubeStreamResolver(downloader, client, testDispatchers)
        val url = resolver.resolveAudioStream("kJQP7kiw5Fk")
        assertNotNull(url)
        assertTrue(url.isNotBlank())
        assertTrue(url.startsWith("http"))
    }

    @Test
    fun testResolveAudioStreamRespectiveTracks() = runBlocking {
        val client = OkHttpClient()
        val downloader = OkHttpDownloader(client)
        val resolver = YouTubeStreamResolver(downloader, client, testDispatchers)
        val url1 = resolver.resolveAudioStream("kJQP7kiw5Fk")
        val url2 = resolver.resolveAudioStream("4NRXx6U8ABQ")
        assertNotNull(url1)
        assertNotNull(url2)
        assertTrue(url1.isNotBlank())
        assertTrue(url2.isNotBlank())
        // Distinct songs MUST resolve to distinct respective streams!
        assertNotEquals("Distinct tracks must have different streams", url1, url2)
    }

    @Test
    fun testCleanVideoId() {
        val client = OkHttpClient()
        val downloader = OkHttpDownloader(client)
        val resolver = YouTubeStreamResolver(downloader, client, testDispatchers)
        assertEquals("abc123XYZ", resolver.cleanVideoId("yt_abc123XYZ"))
        assertEquals("abc123XYZ", resolver.cleanVideoId("abc123XYZ"))
    }

    @Test
    fun testQuickAudioStreamDoesNotPoisonStreamCache() {
        val client = OkHttpClient()
        val downloader = OkHttpDownloader(client)
        val resolver = YouTubeStreamResolver(downloader, client, testDispatchers)
        val quickStream = resolver.getQuickAudioStream("unresolvedVideo123")
        assertNotNull(quickStream)
        // Quick fallback MUST NOT mark the video as resolved in the cache!
        org.junit.Assert.assertFalse(
            "Quick fallback must not poison the stream cache",
            resolver.hasResolvedStream("unresolvedVideo123")
        )
    }

    @Test
    fun testInnerTubeRelatedSongsFetchesRelatedTracks() = runBlocking {
        val client = OkHttpClient()
        val innerTubeClient = com.tharun.mymusic.data.remote.youtube.YouTubeMusicInnerTubeClient(client, testDispatchers)
        // Despacito video ID
        val related = innerTubeClient.getRelatedSongs("kJQP7kiw5Fk")
        assertNotNull(related)
        assertTrue("Related songs radio should return at least 5 songs", related.size >= 5)
        val firstRelated = related.first()
        assertTrue("Related track has non-empty title", firstRelated.title.isNotBlank())
        assertTrue("Related track has non-empty artist", firstRelated.artist.isNotBlank())
        assertTrue("Related track has valid video ID", firstRelated.id.isNotBlank())
    }

    @Test
    fun testInnerTubeSearchSongsOnlyFilter() = runBlocking {
        val client = OkHttpClient()
        val innerTubeClient = com.tharun.mymusic.data.remote.youtube.YouTubeMusicInnerTubeClient(client, testDispatchers)
        val results = innerTubeClient.search("A.R. Rahman")
        assertNotNull(results)
        assertTrue("Search should find songs", results.songs.isNotEmpty())
        // Verified strictly songs only - no videos > 20 mins
        assertTrue(
            "All results must be songs under 20 mins",
            results.songs.all { it.durationMs <= 20 * 60 * 1000L }
        )
    }
}
