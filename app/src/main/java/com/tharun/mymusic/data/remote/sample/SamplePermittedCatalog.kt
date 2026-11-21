package com.tharun.mymusic.data.remote.sample

import com.tharun.mymusic.domain.model.AudioSourceProvider
import com.tharun.mymusic.domain.model.Playlist
import com.tharun.mymusic.domain.model.Song
import kotlin.math.abs

object SamplePermittedCatalog {

    const val DEFAULT_FALLBACK_AUDIO = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"

    val sampleSongs: List<Song> = listOf(
        Song(
            id = "sample_1",
            title = "Clair de Lune (Acoustic Harmony)",
            artist = "SoundHelix Orchestra",
            album = "Suite Bergamasque",
            artworkUri = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            durationMs = 372000L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE,
            isFavorite = true
        ),
        Song(
            id = "sample_2",
            title = "Nocturne in E-flat (Evening Reverie)",
            artist = "Chopin Ensemble",
            album = "Chopin Nocturnes",
            artworkUri = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            durationMs = 423000L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE,
            isFavorite = true
        ),
        Song(
            id = "sample_3",
            title = "Moonlight Sonata (Midnight Waves)",
            artist = "Beethoven Philharmonic",
            album = "Piano Sonata No. 14",
            artworkUri = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            durationMs = 341000L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE,
            isFavorite = false
        ),
        Song(
            id = "sample_4",
            title = "Canon in D Major (Baroque Cadence)",
            artist = "Pachelbel Chamber Trio",
            album = "Baroque Masterpieces",
            artworkUri = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            durationMs = 301000L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE,
            isFavorite = false
        ),
        Song(
            id = "sample_5",
            title = "The Four Seasons: Spring Breeze",
            artist = "Vivaldi Strings",
            album = "The Four Seasons",
            artworkUri = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            durationMs = 352000L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE,
            isFavorite = true
        ),
        Song(
            id = "sample_6",
            title = "Gymnopédie (Serenade in Solitude)",
            artist = "Erik Satie Collective",
            album = "Trois Gymnopédies",
            artworkUri = "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            durationMs = 278000L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE,
            isFavorite = false
        )
    )

    fun getAudioStreamForId(id: String): String {
        val matched = sampleSongs.find { it.id == id }?.mediaUri
        if (!matched.isNullOrBlank()) return matched

        val index = (abs(id.hashCode()) % sampleSongs.size).coerceIn(0, sampleSongs.size - 1)
        return sampleSongs[index].mediaUri ?: DEFAULT_FALLBACK_AUDIO
    }

    val samplePlaylists: List<Playlist> = listOf(
        Playlist(
            id = 1L,
            name = "Peaceful Classical Nights",
            description = "Gentle nocturnal melodies and soothing piano concertos.",
            artworkUri = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            songCount = 6
        ),
        Playlist(
            id = 2L,
            name = "Deep Focus & Study",
            description = "Instrumental masterworks to enhance concentration.",
            artworkUri = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            songCount = 4
        ),
        Playlist(
            id = 3L,
            name = "Acoustic Reflections",
            description = "Harmonic resonance and timeless acoustic arrangements.",
            artworkUri = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            songCount = 5
        )
    )

    val sampleLrcLyrics: Map<String, String> = mapOf(
        "sample_1" to """
            [00:00.00]Claude Debussy - Clair de Lune
            [00:15.00]Soft moonlight gently descends upon the garden
            [00:30.00]A quiet breeze caresses the weeping willows
            [00:48.00]Piano notes ripple like water under silver light
            [01:10.00]The calm stillness of the midnight hour
            [01:35.00]A tender theme arises from the silence
            [02:00.00]Gentle cascading arpeggios embrace the soul
            [02:30.00]The moonlight glimmers as the cadence fades
            [03:00.00]Peace returns to the quiet night
        """.trimIndent(),
        "sample_2" to """
            [00:00.00]Frédéric Chopin - Nocturne in E-flat major, Op. 9, No. 2
            [00:10.00]The opening phrase enters with heartfelt melancholy
            [00:25.00]Each ornament blooms like a delicate evening flower
            [00:45.00]Warm harmonies resonate through the salon
            [01:15.00]A longing melody whispers in sweet sorrow
            [01:45.00]Passionate inflections echo within the chords
            [02:15.00]The cadence resolves into a tranquil dream
            [02:40.00]Fading gently into celestial silence
        """.trimIndent()
    )
}
