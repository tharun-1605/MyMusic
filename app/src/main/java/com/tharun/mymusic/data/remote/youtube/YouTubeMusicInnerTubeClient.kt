package com.tharun.mymusic.data.remote.youtube

import android.util.Log
import com.tharun.mymusic.core.dispatchers.DispatcherProvider
import com.tharun.mymusic.domain.model.Album
import com.tharun.mymusic.domain.model.Artist
import com.tharun.mymusic.domain.model.AudioSourceProvider
import com.tharun.mymusic.domain.model.SearchResult
import com.tharun.mymusic.domain.model.Song
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YouTubeMusicInnerTubeClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val dispatchers: DispatcherProvider
) {
    companion object {
        private const val SEARCH_URL = "https://music.youtube.com/youtubei/v1/search"
        private const val NEXT_URL = "https://music.youtube.com/youtubei/v1/next"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        private const val SONGS_ONLY_PARAMS = "EgWKAQIIAWoKEAkQBRAKEAMQBA%3D%3D"
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }

    suspend fun search(query: String): SearchResult = withContext(dispatchers.io) {
        val q = query.trim()
        if (q.isEmpty()) return@withContext SearchResult(query = q)

        try {
            val payload = JSONObject().apply {
                put("context", JSONObject().apply {
                    put("client", JSONObject().apply {
                        put("clientName", "WEB_REMIX")
                        put("clientVersion", "1.20240101.01.00")
                        put("hl", "en")
                        put("gl", "US")
                    })
                })
                put("query", q)
                put("params", SONGS_ONLY_PARAMS) // Filter strictly for SONGS ONLY
            }

            val request = Request.Builder()
                .url(SEARCH_URL)
                .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
                .header("User-Agent", USER_AGENT)
                .header("Referer", "https://music.youtube.com/")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext SearchResult(query = q)
            }

            val responseBody = response.body?.string() ?: return@withContext SearchResult(query = q)
            parseSearchResult(responseBody, q)
        } catch (e: Exception) {
            Log.e("InnerTubeClient", "Error searching YouTube Music", e)
            SearchResult(query = q)
        }
    }

    suspend fun getTrending(): List<Song> = withContext(dispatchers.io) {
        try {
            val res = search("Top Music Hits")
            res.songs.ifEmpty {
                val res2 = search("Trending Music Hits")
                res2.songs.ifEmpty {
                    search("Popular Music Hits").songs
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRelatedSongs(videoId: String): List<Song> = withContext(dispatchers.io) {
        val cleanId = videoId.trim().removePrefix("yt_")
        if (cleanId.isBlank()) return@withContext emptyList()

        try {
            val payload = JSONObject().apply {
                put("context", JSONObject().apply {
                    put("client", JSONObject().apply {
                        put("clientName", "WEB_REMIX")
                        put("clientVersion", "1.20240101.01.00")
                        put("hl", "en")
                        put("gl", "US")
                    })
                })
                put("videoId", cleanId)
                put("playlistId", "RDAMVM$cleanId")
                put("isAudioOnly", true)
            }

            val request = Request.Builder()
                .url(NEXT_URL)
                .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
                .header("User-Agent", USER_AGENT)
                .header("Referer", "https://music.youtube.com/")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()

            val responseBody = response.body?.string() ?: return@withContext emptyList()
            parseRelatedSongs(responseBody, cleanId)
        } catch (e: Exception) {
            Log.e("InnerTubeClient", "Error fetching related songs for $videoId", e)
            emptyList()
        }
    }

    private fun parseRelatedSongs(jsonStr: String, seedVideoId: String): List<Song> {
        val songs = mutableListOf<Song>()
        try {
            val root = JSONObject(jsonStr)
            val tabs = root.optJSONObject("contents")
                ?.optJSONObject("singleColumnMusicWatchNextResultsRenderer")
                ?.optJSONObject("tabbedRenderer")
                ?.optJSONObject("watchNextTabbedResultsRenderer")
                ?.optJSONArray("tabs") ?: return emptyList()

            for (t in 0 until tabs.length()) {
                val tab = tabs.getJSONObject(t).optJSONObject("tabRenderer") ?: continue
                val queueRenderer = tab.optJSONObject("content")?.optJSONObject("musicQueueRenderer") ?: continue
                val items = queueRenderer.optJSONObject("content")
                    ?.optJSONObject("playlistPanelRenderer")
                    ?.optJSONArray("contents") ?: continue

                for (i in 0 until items.length()) {
                    val renderer = items.getJSONObject(i).optJSONObject("playlistPanelVideoRenderer") ?: continue
                    val vId = renderer.optString("videoId")
                    if (vId.isNullOrBlank() || vId == seedVideoId) continue

                    val titleRuns = renderer.optJSONObject("title")?.optJSONArray("runs")
                    val title = if (titleRuns != null && titleRuns.length() > 0) {
                        val sb = StringBuilder()
                        for (r in 0 until titleRuns.length()) {
                            sb.append(titleRuns.getJSONObject(r).optString("text"))
                        }
                        sb.toString().trim()
                    } else "Unknown Title"

                    val artistRuns = renderer.optJSONObject("shortBylineText")?.optJSONArray("runs")
                        ?: renderer.optJSONObject("longBylineText")?.optJSONArray("runs")
                    val artist = if (artistRuns != null && artistRuns.length() > 0) {
                        val sb = StringBuilder()
                        for (r in 0 until artistRuns.length()) {
                            sb.append(artistRuns.getJSONObject(r).optString("text"))
                        }
                        sb.toString().trim()
                    } else "YouTube Music"

                    val lengthStr = renderer.optJSONObject("lengthText")
                        ?.optJSONArray("runs")
                        ?.optJSONObject(0)
                        ?.optString("text") ?: ""
                    val durationMs = parseDuration(lengthStr)

                    val thumbs = renderer.optJSONObject("thumbnail")?.optJSONArray("thumbnails")
                    val artworkUri = if (thumbs != null && thumbs.length() > 0) {
                        thumbs.getJSONObject(thumbs.length() - 1).optString("url")
                    } else null

                    val cleanTitle = title.replace("&quot;", "\"").replace("&#39;", "'").replace("&amp;", "&")
                    songs.add(
                        Song(
                            id = "yt_$vId",
                            title = cleanTitle,
                            artist = artist,
                            album = "YouTube Music",
                            artworkUri = artworkUri,
                            mediaUri = null,
                            durationMs = durationMs,
                            isPlayable = true,
                            provider = AudioSourceProvider.YOUTUBE_METADATA,
                            youtubeVideoId = vId
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("InnerTubeClient", "Error parsing related songs", e)
        }
        return songs.distinctBy { it.id }
    }

    private fun parseSearchResult(jsonStr: String, query: String): SearchResult {
        val songs = mutableListOf<Song>()

        try {
            val root = JSONObject(jsonStr)
            val tabs = root.optJSONObject("contents")
                ?.optJSONObject("tabbedSearchResultsRenderer")
                ?.optJSONArray("tabs") ?: return SearchResult(query = query)

            if (tabs.length() == 0) return SearchResult(query = query)
            val sections = tabs.getJSONObject(0)
                .optJSONObject("tabRenderer")
                ?.optJSONObject("content")
                ?.optJSONObject("sectionListRenderer")
                ?.optJSONArray("contents") ?: return SearchResult(query = query)

            for (s in 0 until sections.length()) {
                val sec = sections.getJSONObject(s)
                val items = mutableListOf<JSONObject>()

                if (sec.has("musicShelfRenderer")) {
                    val shelf = sec.getJSONObject("musicShelfRenderer")
                    val shelfItems = shelf.optJSONArray("contents")
                    if (shelfItems != null) {
                        for (j in 0 until shelfItems.length()) {
                            items.add(shelfItems.getJSONObject(j))
                        }
                    }
                }
                if (sec.has("musicCardShelfRenderer")) {
                    val cardShelf = sec.getJSONObject("musicCardShelfRenderer")
                    val contents = cardShelf.optJSONArray("contents")
                    if (contents != null) {
                        for (i in 0 until contents.length()) {
                            items.add(contents.getJSONObject(i))
                        }
                    }
                }
                if (sec.has("itemSectionRenderer")) {
                    val itemSec = sec.getJSONObject("itemSectionRenderer")
                    val secContents = itemSec.optJSONArray("contents")
                    if (secContents != null) {
                        for (i in 0 until secContents.length()) {
                            val c = secContents.getJSONObject(i)
                            if (c.has("musicShelfRenderer")) {
                                val shelf = c.getJSONObject("musicShelfRenderer")
                                val shelfItems = shelf.optJSONArray("contents")
                                if (shelfItems != null) {
                                    for (j in 0 until shelfItems.length()) {
                                        items.add(shelfItems.getJSONObject(j))
                                    }
                                }
                            } else if (c.has("musicResponsiveListItemRenderer")) {
                                items.add(c)
                            }
                        }
                    }
                }

                for (item in items) {
                    val mrlir = item.optJSONObject("musicResponsiveListItemRenderer") ?: continue
                    val flexCols = mrlir.optJSONArray("flexColumns") ?: continue
                    if (flexCols.length() == 0) continue

                    val title = extractText(flexCols.getJSONObject(0))
                    if (title.isBlank()) continue

                    val descRuns = if (flexCols.length() > 1) {
                        extractRuns(flexCols.getJSONObject(1))
                    } else emptyList()

                    var artistName = ""
                    var albumName = "YouTube Music"
                    var durationMs = 0L

                    val cleanRuns = descRuns.filter { it != "•" && it.isNotBlank() }
                    val firstText = cleanRuns.getOrNull(0) ?: ""
                    val isSongType = firstText.equals("Song", ignoreCase = true) || firstText.equals("Video", ignoreCase = true)

                    if (isSongType) {
                        artistName = cleanRuns.getOrNull(1) ?: query
                        if (cleanRuns.size > 2) {
                            val last = cleanRuns.last()
                            if (last.contains(":")) {
                                durationMs = parseDuration(last)
                                if (cleanRuns.size > 3) {
                                    albumName = cleanRuns[2]
                                }
                            } else {
                                albumName = cleanRuns[2]
                            }
                        }
                    } else {
                        artistName = firstText.ifBlank { query }
                        val last = cleanRuns.lastOrNull()
                        if (last != null && last.contains(":")) {
                            durationMs = parseDuration(last)
                        }
                    }

                    // Extract Video ID
                    var videoId = mrlir.optJSONObject("playlistItemData")?.optString("videoId")
                    if (videoId.isNullOrBlank()) {
                        videoId = mrlir.optJSONObject("overlay")
                            ?.optJSONObject("musicItemThumbnailOverlayRenderer")
                            ?.optJSONObject("content")
                            ?.optJSONObject("musicPlayButtonRenderer")
                            ?.optJSONObject("playNavigationEndpoint")
                            ?.optJSONObject("watchEndpoint")
                            ?.optString("videoId")
                    }
                    if (videoId.isNullOrBlank()) {
                        videoId = mrlir.optJSONObject("navigationEndpoint")
                            ?.optJSONObject("watchEndpoint")
                            ?.optString("videoId")
                    }

                    // Extract Artwork
                    val thumbUrl = mrlir.optJSONObject("thumbnail")
                        ?.optJSONObject("musicThumbnailRenderer")
                        ?.optJSONObject("thumbnail")
                        ?.optJSONArray("thumbnails")
                        ?.let { thumbs ->
                            if (thumbs.length() > 0) {
                                thumbs.getJSONObject(thumbs.length() - 1).optString("url")
                            } else null
                        }

                    // Exclude non-songs or excessively long items (> 20 min)
                    if (!videoId.isNullOrBlank() && durationMs <= 1200000L) {
                        val cleanTitle = title.replace("&quot;", "\"").replace("&#39;", "'").replace("&amp;", "&")
                        songs.add(
                            Song(
                                id = "yt_$videoId",
                                title = cleanTitle,
                                artist = artistName.ifBlank { "YouTube Music" },
                                album = albumName,
                                artworkUri = thumbUrl,
                                mediaUri = null,
                                durationMs = durationMs,
                                isPlayable = true,
                                provider = AudioSourceProvider.YOUTUBE_METADATA,
                                youtubeVideoId = videoId
                            )
                        )
                    }
                }
            }

            val distinctSongs = songs.distinctBy { it.id }
            val distinctArtists = distinctSongs.map { it.artist }.distinct().map {
                Artist(id = "yt_artist_$it", name = it, imageUrl = null)
            }
            val distinctAlbums = distinctSongs.map { it.album }.filter { it.isNotBlank() }.distinct().map {
                Album(id = "yt_album_$it", title = it, artist = "", artworkUri = null)
            }

            return SearchResult(
                songs = distinctSongs,
                artists = distinctArtists,
                albums = distinctAlbums,
                query = query
            )
        } catch (e: Exception) {
            Log.e("InnerTubeClient", "Error parsing search result", e)
            return SearchResult(query = query)
        }
    }

    private fun extractText(col: JSONObject): String {
        val runs = col.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
            ?.optJSONObject("text")
            ?.optJSONArray("runs") ?: return ""
        val sb = StringBuilder()
        for (i in 0 until runs.length()) {
            sb.append(runs.getJSONObject(i).optString("text"))
        }
        return sb.toString().trim()
    }

    private fun extractRuns(col: JSONObject): List<String> {
        val runs = col.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
            ?.optJSONObject("text")
            ?.optJSONArray("runs") ?: return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until runs.length()) {
            val text = runs.getJSONObject(i).optString("text").trim()
            if (text.isNotEmpty()) {
                list.add(text)
            }
        }
        return list
    }

    private fun parseDuration(timeStr: String): Long {
        val parts = timeStr.split(":")
        return try {
            when (parts.size) {
                2 -> (parts[0].toLong() * 60 + parts[1].toLong()) * 1000L
                3 -> (parts[0].toLong() * 3600 + parts[1].toLong() * 60 + parts[2].toLong()) * 1000L
                else -> 0L
            }
        } catch (_: Exception) {
            0L
        }
    }
}
