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
}
