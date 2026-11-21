package com.tharun.mymusic

import com.tharun.mymusic.core.dispatchers.DispatcherProvider
import com.tharun.mymusic.data.remote.stream.OkHttpDownloader
import com.tharun.mymusic.data.remote.stream.YouTubeStreamResolver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Assert.assertNotNull
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
    }
}
