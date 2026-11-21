package com.tharun.mymusic.data.remote.stream

import android.util.Log
import com.tharun.mymusic.core.dispatchers.DispatcherProvider
import com.tharun.mymusic.data.remote.sample.SamplePermittedCatalog
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.StreamInfo
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YouTubeStreamResolver @Inject constructor(
    private val okHttpDownloader: OkHttpDownloader,
    private val okHttpClient: OkHttpClient,
    private val dispatchers: DispatcherProvider
) {
    companion object {
        private const val TAG = "YouTubeStreamResolver"
        private const val PLAYER_URL = "https://www.youtube.com/youtubei/v1/player?prettyPrint=false"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        private const val IOS_PRODUCER_UA = "com.google.ios.youtubeproducer/0.147.945 (iPhone16,2; U; CPU iOS 18_3_2 like Mac OS X;)"
    }

    private val streamCache = ConcurrentHashMap<String, String>()
    private var isNewPipeInitialized = false

    init {
        try {
            NewPipe.init(okHttpDownloader)
            isNewPipeInitialized = true
        } catch (_: Throwable) {
            isNewPipeInitialized = false
        }
    }

    fun cleanVideoId(idOrVideoId: String): String {
        return idOrVideoId.trim().removePrefix("yt_")
    }

    fun isStreamValid(streamUrl: String?): Boolean {
        if (streamUrl.isNullOrBlank()) return false
        return streamUrl.contains("googlevideo.com") ||
                streamUrl.startsWith("content://") ||
                streamUrl.startsWith("file://")
    }

    fun hasResolvedStream(idOrVideoId: String): Boolean {
        val cleanId = cleanVideoId(idOrVideoId)
        val cached = streamCache[cleanId]
        return isStreamValid(cached)
    }

    fun getQuickAudioStream(id: String): String {
        val cleanId = cleanVideoId(id)
        val cached = streamCache[cleanId]
        if (!cached.isNullOrBlank()) return cached
        // Fallback without poisoning the cache with sample audio
        return SamplePermittedCatalog.getAudioStreamForId(cleanId)
    }

    fun getCachedStream(idOrVideoId: String): String? {
        val cleanId = cleanVideoId(idOrVideoId)
        val cached = streamCache[cleanId]
        return if (!cached.isNullOrBlank()) cached else null
    }

    suspend fun resolveAudioStream(idOrVideoId: String): String = withContext(dispatchers.io) {
        val videoId = cleanVideoId(idOrVideoId)
        if (videoId.isBlank()) {
            return@withContext SamplePermittedCatalog.DEFAULT_FALLBACK_AUDIO
        }

        streamCache[videoId]?.let { return@withContext it }

        // Tier 1: InnerTube direct extraction with IOS_PRODUCER (instant success rate, direct googlevideo URL, no bot-check)
        try {
            val streamUrl = extractViaInnerTube(videoId)
            if (!streamUrl.isNullOrBlank()) {
                streamCache[videoId] = streamUrl
                return@withContext streamUrl
            }
        } catch (e: Throwable) {
            safeLog(TAG, "Tier 1 InnerTube extraction failed for $videoId", e)
        }

        // Tier 2: Extract via NewPipeExtractor if available
        if (isNewPipeInitialized) {
            try {
                val streamInfo = StreamInfo.getInfo("https://www.youtube.com/watch?v=$videoId")
                val audioStreams = streamInfo.audioStreams
                if (!audioStreams.isNullOrEmpty()) {
                    val bestStream: AudioStream = audioStreams
                        .filter { !it.content.isNullOrBlank() || !it.url.isNullOrBlank() }
                        .maxByOrNull { it.averageBitrate.takeIf { b -> b > 0 } ?: it.bitrate }
                        ?: audioStreams.first()

                    val url = bestStream.content ?: bestStream.url
                    if (!url.isNullOrBlank()) {
                        streamCache[videoId] = url
                        return@withContext url
                    }
                }
            } catch (e: Throwable) {
                safeLog(TAG, "Tier 2 NewPipe extraction failed for $videoId", e)
            }
        }

        // Tier 3: Permitted sample catalog fallback
        val fallback = SamplePermittedCatalog.getAudioStreamForId(videoId)
        return@withContext fallback
    }

    private fun extractViaInnerTube(videoId: String): String? {
        val clientContext = JSONObject().apply {
            put("clientName", "IOS_PRODUCER")
            put("clientVersion", "0.147.945")
            put("deviceMake", "Apple")
            put("deviceModel", "iPhone16,2")
            put("osName", "iPhone")
            put("osVersion", "18.3.2.22D82")
            put("hl", "en")
            put("gl", "US")
        }

        val payload = JSONObject().apply {
            put("context", JSONObject().apply {
                put("client", clientContext)
            })
            put("videoId", videoId)
            put("contentCheckOk", true)
            put("racyCheckOk", true)
        }

        val request = Request.Builder()
            .url(PLAYER_URL)
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .header("User-Agent", IOS_PRODUCER_UA)
            .header("X-Goog-Api-Format-Version", "2")
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) return null

        val responseBody = response.body?.string() ?: return null
        val json = JSONObject(responseBody)
        val streamingData = json.optJSONObject("streamingData") ?: return null

        // 1. Check adaptive formats (audio/mp4 m4a, audio/webm opus)
        val adaptiveFormats = streamingData.optJSONArray("adaptiveFormats")
        if (adaptiveFormats != null && adaptiveFormats.length() > 0) {
            var bestUrl: String? = null
            var bestBitrate = -1
            for (i in 0 until adaptiveFormats.length()) {
                val format = adaptiveFormats.getJSONObject(i)
                val mime = format.optString("mimeType", "")
                val url = format.optString("url", "")
                if (mime.contains("audio", ignoreCase = true) && url.isNotBlank()) {
                    val bitrate = format.optInt("bitrate", 0)
                    if (bitrate > bestBitrate) {
                        bestBitrate = bitrate
                        bestUrl = url
                    }
                }
            }
            if (!bestUrl.isNullOrBlank()) {
                return bestUrl
            }
        }

        // 2. Check progressive formats as backup
        val formats = streamingData.optJSONArray("formats")
        if (formats != null && formats.length() > 0) {
            for (i in 0 until formats.length()) {
                val format = formats.getJSONObject(i)
                val url = format.optString("url", "")
                if (url.isNotBlank()) {
                    return url
                }
            }
        }

        return null
    }

    fun getOrResolveStreamSync(idOrVideoId: String): String {
        val cleanId = cleanVideoId(idOrVideoId)
        streamCache[cleanId]?.let { return it }

        // Attempt synchronous extraction if not on Main thread
        val isMainThread = try {
            android.os.Looper.myLooper() != null && android.os.Looper.myLooper() == android.os.Looper.getMainLooper()
        } catch (_: Throwable) {
            false
        }

        if (!isMainThread) {
            try {
                val stream = extractViaInnerTube(cleanId)
                if (!stream.isNullOrBlank()) {
                    streamCache[cleanId] = stream
                    return stream
                }
            } catch (_: Throwable) {}
        }

        return getQuickAudioStream(cleanId)
    }

    private fun safeLog(tag: String, message: String, throwable: Throwable? = null) {
        try {
            if (throwable != null) {
                Log.w(tag, message, throwable)
            } else {
                Log.w(tag, message)
            }
        } catch (_: Throwable) {
            // Avoid crashing in local unit tests where Log is not mocked
        }
    }
}
