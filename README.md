# MyMusic - Production-Quality Android Music Application

**MyMusic** (`com.tharun.mymusic`) is a production-quality, compliant modern Android music application built from the ground up using **Kotlin**, **Jetpack Compose + Material 3**, **Clean Architecture + MVVM**, **Room Database**, **DataStore Preferences**, **Hilt Dependency Injection**, and **AndroidX Media3 (ExoPlayer & MediaSession)**.

---

## 🎵 Key Features

1. **Splash Screen & Branding**: Smooth spring-animated branded entrance that automatically pre-populates the sample catalog and playlists on first launch.
2. **Home Screen**:
   - Dynamic time-of-day greeting ("Good morning", "Good afternoon", "Good evening", "Late night vibes").
   - Horizontal featured playlists carousel.
   - Recently played tracks.
   - Trending and discover section.
3. **Music Discovery & Search**:
   - 500ms debounced search input.
   - Filter chips: **All**, **Playable Tracks**, and **YouTube Discovery**.
   - Recent search query tags with individual deletion and clear all.
   - Graceful offline, empty, and quota handling.
4. **Playback & Now Playing**:
   - Fullscreen player with high-resolution artwork, glow shadow, and animated progress slider.
   - Play, pause, skip next, skip previous, seek to position.
   - Repeat modes (**Off**, **All**, **One**) and **Shuffle** toggle.
   - Favorite heart toggle button.
   - Quick action pills for **Lyrics**, **Equalizer**, and **Play Queue**.
5. **Persistent Mini-Player**:
   - Docked above bottom navigation across Home, Search, Library, and Settings.
   - Artwork thumbnail, track title, artist, progress indicator, play/pause, and skip buttons.
   - Tap to expand into fullscreen Now Playing.
6. **Queue Management**:
   - Reorder and remove tracks from playback queue.
   - Displays currently playing indicator and track numbers.
   - Clear queue button.
7. **Library & Playlists**:
   - Tabs for **Songs**, **Playlists**, **Favorites**, and **Artists**.
   - Playlist creation modal with title and description.
   - Playlist detail view with play all, shuffle all, rename playlist, delete playlist, remove track, and reorder.
   - Device storage scanner (`MediaStore.Audio.Media`) for user-owned local audio files.
8. **Synchronized LRC Lyrics**:
   - Full support for LRC timestamped format (`[mm:ss.xx]`) and plain-text lyrics.
   - Real-time line highlighting and auto-scrolling synced to playback position.
   - Tap any lyric line to jump playback directly to that timestamp.
   - Detects manual scrolling and displays a floating *"Sync with audio"* button.
   - Offline caching in Room database.
9. **Hardware Equalizer & Audio FX**:
   - Attaches to active playback audio session ID.
   - Presets: **Normal**, **Bass Boost**, **Treble Boost**, **Rock**, **Pop**, **Classical**, **Electronic**, **Custom**.
   - Bass Boost & Virtualizer strength dials (0% - 100%).
   - 5-band graphic equalizer with dB gain sliders (-15 dB to +15 dB).
   - Graceful fallback and user guidance if hardware effects are unavailable.
   - Persisted in DataStore preferences.
10. **Android Auto Integration**:
    - Extends Media3 `MediaLibraryService`.
    - Declared in `AndroidManifest.xml` with `com.google.android.gms.car.application` and `@xml/automotive_app_desc`.
    - Exposes browsable hierarchy:
      - `Discover & Samples`
      - `Favorites`
      - `Recently Played`
      - `Playlists`
    - MediaSession transport controls.
11. **Settings & Themes**:
    - Theme switching: **Dark Theme** (deep charcoal/black with neon coral accents), **Light Theme**, or **System Default**.
    - YouTube Data API v3 key configuration dialog.
    - Cache management: Clear search history, clear playback history, reload sample catalog.
    - Legal compliance statements.

---

## ⚖️ Integration & Legal Compliance Rules

In strict adherence to product requirements and YouTube Terms of Service:
- **Separation of Metadata and Playback**: YouTube Data API v3 is used strictly for search and metadata discovery (title, artist, channel, high-res thumbnail, duration).
- **No Stream Extraction**: Audio streams are **never** extracted, ripped, or reverse-engineered from YouTube URLs, and YouTube streams are never fed into ExoPlayer.
- **Compliant Handoff**: For YouTube discovery items (`AudioSourceProvider.YOUTUBE_METADATA`), tapping the item opens an informative compliant dialog with an **"Open in YouTube"** action, launching the official YouTube Android app (`vnd.youtube:<id>`) or official web browser player.
- **Permitted & Local Playback**: The app includes a rich catalog of licensed/Creative Commons audio streams (Debussy, Chopin, Beethoven, Pachelbel, Vivaldi, Erik Satie) and scans user-owned local device audio files (`MediaStore`), ensuring complete out-of-the-box playback functionality.
- **API Key Security**: Secrets are never hardcoded. API keys are loaded via `local.properties` / `BuildConfig` or configured dynamically via the user settings screen.

---

## 🏛️ Clean Architecture & Tech Stack

```
com.tharun.mymusic
├── core
│   ├── dispatchers      # DispatcherProvider (Main, IO, Default)
│   ├── model            # Resource<T>, UiState<T>
│   ├── theme            # Material 3 Charcoal Dark & Light themes, Gradients, Shapes, Typography
│   ├── ui               # Reusable UI components (AlbumArt, SongItemRow, MiniPlayer, GlowingPlayButton)
│   └── util             # LrcParser, TimeUtils
├── data
│   ├── local
│   │   ├── dao          # Room DAOs (SongDao, PlaylistDao, FavoriteDao, RecentlyPlayedDao, SearchHistoryDao, LyricsDao)
│   │   ├── db           # MyMusicDatabase
│   │   ├── entity       # Room Entities & Cross-References
│   │   └── preferences  # UserPreferencesDataStore (Theme, EQ settings, API key)
│   ├── remote
│   │   ├── discovery    # SampleDiscoveryProvider, YouTubeDiscoveryProvider, LocalDeviceDiscoveryProvider
│   │   ├── sample       # SamplePermittedCatalog (Licensed Creative Commons tracks + LRC lyrics)
│   │   └── youtube      # YouTubeApiService (Retrofit), DTOs (Kotlinx Serialization)
│   └── repository       # Repository Implementations
├── domain
│   ├── model            # Immutable domain models (Song, Album, Artist, Playlist, Lyrics, PlaybackState, EqualizerConfig)
│   ├── repository       # Repository Interfaces
│   └── usecase          # Use Cases (GetHomeData, SearchMusic, ManagePlaylist, ToggleFavorite, GetLyrics, ScanDeviceAudio)
├── feature
│   ├── equalizer        # EqualizerScreen, EqualizerViewModel
│   ├── home             # HomeScreen, HomeViewModel
│   ├── library          # LibraryScreen, LibraryViewModel
│   ├── lyrics           # LyricsScreen, LyricsViewModel
│   ├── player           # NowPlayingScreen, QueueScreen, PlayerViewModel
│   ├── playlists        # PlaylistDetailScreen, PlaylistDetailViewModel
│   ├── search           # SearchScreen, SearchViewModel
│   ├── settings         # SettingsScreen, SettingsViewModel
│   └── splash           # SplashScreen
├── service              # MyMusicPlaybackService (MediaLibraryService), AudioEffectController, BecomingNoisyReceiver
├── navigation           # NavGraph, Screen routes, Bottom Navigation Bar
└── di                   # Hilt Modules (DatabaseModule, NetworkModule, RepositoryModule, DispatchersModule)
```

---

## 🛠️ Build and Test Commands

### 1. Prerequisites
- **JDK**: Version 21
- **Android SDK**: Compile SDK 35, Min SDK 26
- **Gradle**: 8.12 (via bundled `./gradlew`)

### 2. Configure YouTube Data API Key (Optional)
To enable YouTube music discovery search:
1. Open `local.properties` in the project root:
   ```properties
   sdk.dir=/home/tharun/Android/Sdk
   YOUTUBE_API_KEY=YOUR_YOUTUBE_DATA_API_V3_KEY
   ```
2. Alternatively, launch the app and navigate to **Settings > YouTube Data API v3 Key** to enter or update your key at runtime.

### 3. Run Unit Tests & Database Tests
Execute all unit tests and Room in-memory database tests:
```bash
./gradlew testDebugUnitTest
```

### 4. Build Debug APK
Build the full production-ready debug APK:
```bash
./gradlew assembleDebug
```
The compiled APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 Testing Coverage

The automated test suite verifies:
- **`LrcParserTest`**: Parsing timestamped LRC files, binary-search synchronized line calculation across all playback intervals, and handling plain-text/empty lyrics.
- **`TimeUtilsTest`**: Formatting milliseconds to `mm:ss` and parsing ISO 8601 duration strings (`PT3M45S`).
- **`RoomDatabaseTest`**: In-memory database testing of `SongDao`, `PlaylistDao`, `FavoriteDao`, `RecentlyPlayedDao`, `SearchHistoryDao`, and `LyricsDao`, including playlist song ordering and transactions.
- **`UseCasesTest`**: Business logic testing of `GetHomeDataUseCase`, `SearchMusicUseCase`, `ToggleFavoriteUseCase`, and `ManagePlaylistUseCase`.
- **`PlayerViewModelTest`**: Play, pause, skip, seek, shuffle, and repeat mode cycling.
- **`SearchViewModelTest`**: Search input debouncing, recent searches persistence, and filter switching.
- **`LyricsViewModelTest`**: Synchronization with audio position, user manual scroll pause, and auto-scroll resumption.
- **`EqualizerViewModelTest`**: Preset selection, bass boost, virtualizer, and custom frequency band level adjustments.

---

## 📱 Android Auto Verification
The service declares full compatibility with Android Auto:
- Service declaration: `androidx.media3.session.MediaLibraryService` with action `android.media.browse.MediaBrowserService`.
- Media hierarchy implementation in `onGetLibraryRoot` and `onGetChildren`.
- Automotive application descriptor at `app/src/main/res/xml/automotive_app_desc.xml`.
