package com.tharun.mymusic.domain.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val artworkUri: String? = null,
    val mediaUri: String? = null,
    val durationMs: Long = 0L,
    val isPlayable: Boolean = true,
    val provider: AudioSourceProvider = AudioSourceProvider.SAMPLE,
    val youtubeVideoId: String? = null,
    val isFavorite: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)

enum class AudioSourceProvider {
    SAMPLE,
    LOCAL,
    YOUTUBE_METADATA
}

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val artworkUri: String? = null,
    val songCount: Int = 0
)

data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val trackCount: Int = 0
)

data class Playlist(
    val id: Long = 0L,
    val name: String,
    val description: String? = null,
    val artworkUri: String? = null,
    val songCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class Lyrics(
    val songId: String,
    val plainText: String? = null,
    val lines: List<LyricsLine> = emptyList(),
    val isSynced: Boolean = lines.isNotEmpty()
)

data class LyricsLine(
    val timestampMs: Long,
    val text: String
)

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

data class PlaybackState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val isShuffleEnabled: Boolean = false,
    val queue: List<Song> = emptyList(),
    val currentQueueIndex: Int = -1
)

enum class EqualizerPreset {
    NORMAL,
    BASS_BOOST,
    TREBLE_BOOST,
    ROCK,
    POP,
    CLASSICAL,
    ELECTRONIC,
    CUSTOM
}

data class EqualizerBand(
    val index: Int,
    val centerFreqHz: Int,
    val gainDb: Float,
    val minDb: Float,
    val maxDb: Float
)

data class EqualizerConfig(
    val isEnabled: Boolean = false,
    val preset: EqualizerPreset = EqualizerPreset.NORMAL,
    val bassBoostLevel: Int = 0,
    val virtualizerLevel: Int = 0,
    val bandLevels: Map<Int, Int> = emptyMap()
)

data class SearchResult(
    val songs: List<Song> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val query: String = ""
)
