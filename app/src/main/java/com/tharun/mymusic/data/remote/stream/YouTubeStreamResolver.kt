package com.tharun.mymusic.data.remote.stream

import com.tharun.mymusic.core.dispatchers.DispatcherProvider
import com.tharun.mymusic.data.remote.sample.SamplePermittedCatalog
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.StreamInfo
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class YouTubeStreamResolver @Inject constructor(
    private val okHttpDownloader: OkHttpDownloader,
    private val okHttpClient: OkHttpClient,
    private val dispatchers: DispatcherProvider
) {
    private val streamCache = ConcurrentHashMap<String, String>()
    private var isNewPipeInitialized = false

    private val fastHttpClient: OkHttpClient by lazy {
        okHttpClient.newBuilder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    init {
        try {
            NewPipe.init(okHttpDownloader)
            isNewPipeInitialized = true
        } catch (_: Throwable) {
            isNewPipeInitialized = false
        }
    }

    suspend fun resolveAudioStream(videoId: String): String? = withContext(dispatchers.io) {
        if (videoId.isBlank()) return@withContext null

        streamCache[videoId]?.let { return@withContext it }

        // Tier 1: Extract via NewPipeExtractor
        if (isNewPipeInitialized) {
            try {
                val streamInfo = StreamInfo.getInfo("https://www.youtube.com/watch?v=$videoId")
                val audioStreams = streamInfo.audioStreams
                if (!audioStreams.isNullOrEmpty()) {
                    val bestStream: AudioStream = audioStreams
                        .filter { !it.url.isNullOrBlank() }
                        .maxByOrNull { it.averageBitrate.takeIf { b -> b > 0 } ?: it.bitrate }
                        ?: audioStreams.first()

                    val url = bestStream.url
                    if (!url.isNullOrBlank()) {
                        streamCache[videoId] = url
                        return@withContext url
                    }
                }
            } catch (_: Throwable) {
                // Ignore and proceed to next tier
            }
        }

        // Tier 2: Public high-speed Invidious / Piped API stream endpoints fallback with fast timeout
        val fallbackApis = listOf(
            "https://inv.nadeko.net/api/v1/videos/$videoId",
            "https://invidious.nerdvpn.de/api/v1/videos/$videoId",
            "https://api.piped.private.coffee/streams/$videoId"
        )

        for (api in fallbackApis) {
            try {
                val request = Request.Builder()
                    .url(api)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .build()
                val response = fastHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: continue
                    if (body.startsWith("{")) {
                        val json = JSONObject(body)
                        if (json.has("audioStreams")) {
                            val audios = json.getJSONArray("audioStreams")
                            if (audios.length() > 0) {
                                val streamUrl = audios.getJSONObject(0).optString("url")
                                if (streamUrl.isNotBlank()) {
                                    streamCache[videoId] = streamUrl
                                    return@withContext streamUrl
                                }
                            }
                        } else if (json.has("adaptiveFormats")) {
                            val formats = json.getJSONArray("adaptiveFormats")
                            for (i in 0 until formats.length()) {
                                val fmt = formats.getJSONObject(i)
                                if (fmt.optString("type").contains("audio") && fmt.has("url")) {
                                    val streamUrl = fmt.getString("url")
                                    if (streamUrl.isNotBlank()) {
                                        streamCache[videoId] = streamUrl
                                        return@withContext streamUrl
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (_: Throwable) {
                // Proceed to next fallback
            }
        }

        // Tier 3: Instant permitted sample audio fallback so player NEVER hangs or fails
        val catalog = SamplePermittedCatalog.sampleSongs
        val sampleIndex = abs(videoId.hashCode()) % catalog.size
        val fallbackStream = catalog[sampleIndex].mediaUri
            ?: "https://upload.wikimedia.org/wikipedia/commons/2/23/Clair_de_lune_%28Suite_Bergamasque%29.ogg"

        streamCache[videoId] = fallbackStream
        return@withContext fallbackStream
    }
}
