package com.tharun.mymusic.data.remote.sample

import com.tharun.mymusic.domain.model.AudioSourceProvider
import com.tharun.mymusic.domain.model.Playlist
import com.tharun.mymusic.domain.model.Song

object SamplePermittedCatalog {

    val sampleSongs: List<Song> = listOf(
        Song(
            id = "sample_1",
            title = "Clair de Lune",
            artist = "Claude Debussy",
            album = "Suite Bergamasque",
            artworkUri = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://upload.wikimedia.org/wikipedia/commons/2/23/Clair_de_lune_%28Suite_Bergamasque%29.ogg",
            durationMs = 304000L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE,
            isFavorite = true
        ),
        Song(
            id = "sample_2",
            title = "Nocturne in E-flat major, Op. 9, No. 2",
            artist = "Frédéric Chopin",
            album = "Chopin Nocturnes",
            artworkUri = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://upload.wikimedia.org/wikipedia/commons/2/21/Chopin_Nocturne_Op._9_No._2.ogg",
            durationMs = 265000L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE,
            isFavorite = true
        ),
        Song(
            id = "sample_3",
            title = "Moonlight Sonata (Adagio sostenuto)",
            artist = "Ludwig van Beethoven",
            album = "Piano Sonata No. 14",
            artworkUri = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://upload.wikimedia.org/wikipedia/commons/e/eb/Beethoven_Moonlight_1st_movement.ogg",
            durationMs = 320000L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE,
            isFavorite = false
        ),
        Song(
            id = "sample_4",
            title = "Canon in D Major",
            artist = "Johann Pachelbel",
            album = "Baroque Masterpieces",
            artworkUri = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://upload.wikimedia.org/wikipedia/commons/7/7b/Johann_Pachelbel_-_Canon_in_D_major.ogg",
            durationMs = 384000L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE,
            isFavorite = false
        ),
        Song(
            id = "sample_5",
            title = "The Four Seasons: Spring",
            artist = "Antonio Vivaldi",
            album = "The Four Seasons",
            artworkUri = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://upload.wikimedia.org/wikipedia/commons/3/3c/Vivaldi_Spring_mvt_1_Allegro.ogg",
            durationMs = 216000L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE,
            isFavorite = true
        ),
        Song(
            id = "sample_6",
            title = "Gymnopédie No. 1",
            artist = "Erik Satie",
            album = "Trois Gymnopédies",
            artworkUri = "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=600&auto=format&fit=crop&q=80",
            mediaUri = "https://upload.wikimedia.org/wikipedia/commons/e/e4/Erik_Satie_-_Gymnop%C3%A9die_No._1.ogg",
            durationMs = 203000L,
            isPlayable = true,
            provider = AudioSourceProvider.SAMPLE,
            isFavorite = false
        )
    )

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
