package com.tharun.mymusic.data.remote.discovery

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.tharun.mymusic.BuildConfig
import com.tharun.mymusic.core.util.TimeUtils
import com.tharun.mymusic.data.local.preferences.UserPreferencesDataStore
import com.tharun.mymusic.data.remote.sample.SamplePermittedCatalog
import com.tharun.mymusic.data.remote.youtube.YouTubeApiService
import com.tharun.mymusic.domain.model.Album
import com.tharun.mymusic.domain.model.Artist
import com.tharun.mymusic.domain.model.AudioSourceProvider
import com.tharun.mymusic.domain.model.SearchResult
import com.tharun.mymusic.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

interface MusicDiscoveryProvider {
    val providerName: String
    val isConfigured: Boolean
    suspend fun search(query: String): SearchResult
    suspend fun getTrending(): List<Song>
    suspend fun getRelated(videoId: String): List<Song>
}

@Singleton
class SampleDiscoveryProvider @Inject constructor() : MusicDiscoveryProvider {
    override val providerName: String = "Permitted Sample Catalog"
    override val isConfigured: Boolean = true

    override suspend fun search(query: String): SearchResult {
        val q = query.trim().lowercase()
        val matchedSongs = SamplePermittedCatalog.sampleSongs.filter {
            it.title.lowercase().contains(q) ||
                    it.artist.lowercase().contains(q) ||
                    it.album.lowercase().contains(q)
        }
        val matchedArtists = matchedSongs.map { it.artist }.distinct().map {
            Artist(id = it, name = it, imageUrl = null, trackCount = 1)
        }
        val matchedAlbums = matchedSongs.map { it.album }.distinct().filter { it.isNotEmpty() }.map {
            Album(id = it, title = it, artist = "", artworkUri = null, songCount = 1)
        }
        return SearchResult(
            songs = matchedSongs,
            artists = matchedArtists,
            albums = matchedAlbums,
            query = query
        )
    }

    override suspend fun getTrending(): List<Song> {
        return SamplePermittedCatalog.sampleSongs
    }

    override suspend fun getRelated(videoId: String): List<Song> {
        return SamplePermittedCatalog.sampleSongs.filter { it.id != videoId }
    }
}

@Singleton
class YouTubeDiscoveryProvider @Inject constructor(
    private val youTubeApiService: YouTubeApiService,
    private val preferencesDataStore: UserPreferencesDataStore,
    private val innerTubeClient: com.tharun.mymusic.data.remote.youtube.YouTubeMusicInnerTubeClient
) : MusicDiscoveryProvider {

    override val providerName: String = "YouTube Music (InnerTube / SimpMusic)"

    override val isConfigured: Boolean
        get() = true

    private suspend fun getEffectiveApiKey(): String {
        val userKey = preferencesDataStore.youtubeApiKey.first()
        return when {
            userKey.isNotBlank() -> userKey
            BuildConfig.YOUTUBE_API_KEY.isNotBlank() -> BuildConfig.YOUTUBE_API_KEY
            else -> ""
        }
    }

    override suspend fun search(query: String): SearchResult {
        // First try InnerTube directly without requiring any API key (SimpMusic architecture)
        val innerTubeResults = innerTubeClient.search(query)
        if (innerTubeResults.songs.isNotEmpty()) {
            return innerTubeResults
        }

        // Optional fallback to YouTube Data API v3 if API key is supplied
        val apiKey = getEffectiveApiKey()
        if (apiKey.isBlank()) {
            return innerTubeResults
        }

        return try {
            val response = youTubeApiService.searchMusic(
                query = query,
                apiKey = apiKey
            )

            val videoIds = response.items.mapNotNull { it.id?.videoId }.filter { it.isNotBlank() }
            if (videoIds.isEmpty()) return SearchResult(query = query)

            // Fetch video durations & details in batch
            val detailsResponse = try {
                youTubeApiService.getVideoDetails(
                    ids = videoIds.joinToString(","),
                    apiKey = apiKey
                )
            } catch (_: Exception) {
                null
            }

            val durationMap = detailsResponse?.items?.associate {
                it.id to TimeUtils.parseDurationIso(it.contentDetails?.duration)
            } ?: emptyMap()

            val songs = response.items.mapNotNull { item ->
                val videoId = item.id?.videoId ?: return@mapNotNull null
                val snippet = item.snippet ?: return@mapNotNull null
                val artwork = snippet.thumbnails?.high?.url
                    ?: snippet.thumbnails?.medium?.url
                    ?: snippet.thumbnails?.default?.url

                Song(
                    id = "yt_$videoId",
                    title = snippet.title.replace("&quot;", "\"").replace("&#39;", "'").replace("&amp;", "&"),
                    artist = snippet.channelTitle,
                    album = "YouTube Music",
                    artworkUri = artwork,
                    mediaUri = null,
                    durationMs = durationMap[videoId] ?: 0L,
                    isPlayable = true,
                    provider = AudioSourceProvider.YOUTUBE_METADATA,
                    youtubeVideoId = videoId
                )
            }

            val artists = songs.map { it.artist }.distinct().map {
                Artist(id = "yt_artist_$it", name = it, imageUrl = null)
            }

            SearchResult(
                songs = songs,
                artists = artists,
                albums = emptyList(),
                query = query
            )
        } catch (_: Exception) {
            innerTubeResults
        }
    }

    override suspend fun getTrending(): List<Song> {
        val innerTubeTrending = innerTubeClient.getTrending()
        if (innerTubeTrending.isNotEmpty()) {
            return innerTubeTrending
        }

        val apiKey = getEffectiveApiKey()
        if (apiKey.isBlank()) return emptyList()

        return try {
            val response = youTubeApiService.searchMusic(
                query = "Top Music Hits",
                maxResults = 10,
                apiKey = apiKey
            )
            response.items.mapNotNull { item ->
                val videoId = item.id?.videoId ?: return@mapNotNull null
                val snippet = item.snippet ?: return@mapNotNull null
                Song(
                    id = "yt_$videoId",
                    title = snippet.title.replace("&quot;", "\"").replace("&#39;", "'").replace("&amp;", "&"),
                    artist = snippet.channelTitle,
                    album = "YouTube Music",
                    artworkUri = snippet.thumbnails?.high?.url ?: snippet.thumbnails?.medium?.url,
                    mediaUri = null,
                    durationMs = 0L,
                    isPlayable = true,
                    provider = AudioSourceProvider.YOUTUBE_METADATA,
                    youtubeVideoId = videoId
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getRelated(videoId: String): List<Song> {
        return innerTubeClient.getRelatedSongs(videoId)
    }
}

@Singleton
class LocalDeviceDiscoveryProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scanDeviceAudio(): List<Song> {
        val songs = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                "${MediaStore.Audio.Media.TITLE} ASC"
            )

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val title = it.getString(titleCol) ?: "Unknown"
                    val artist = it.getString(artistCol) ?: "Unknown Artist"
                    val album = it.getString(albumCol) ?: "Unknown Album"
                    val duration = it.getLong(durationCol)
                    val albumId = it.getLong(albumIdCol)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    ).toString()

                    val artworkUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    ).toString()

                    songs.add(
                        Song(
                            id = "local_$id",
                            title = title,
                            artist = artist,
                            album = album,
                            artworkUri = artworkUri,
                            mediaUri = contentUri,
                            durationMs = duration,
                            isPlayable = true,
                            provider = AudioSourceProvider.LOCAL
                        )
                    )
                }
            }
        } catch (_: Exception) {
            // Permission or security exception on ungranted storage
        }
        return songs
    }
}
