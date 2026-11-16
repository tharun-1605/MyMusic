package com.tharun.mymusic.service

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import com.tharun.mymusic.MainActivity
import com.tharun.mymusic.data.local.dao.FavoriteDao
import com.tharun.mymusic.data.local.dao.PlaylistDao
import com.tharun.mymusic.data.local.dao.RecentlyPlayedDao
import com.tharun.mymusic.data.local.dao.SongDao
import com.tharun.mymusic.data.local.entity.RecentlyPlayedEntity
import com.tharun.mymusic.data.local.preferences.UserPreferencesDataStore
import com.tharun.mymusic.data.remote.sample.SamplePermittedCatalog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyMusicPlaybackService : MediaLibraryService() {

    @Inject lateinit var audioEffectController: AudioEffectController
    @Inject lateinit var preferencesDataStore: UserPreferencesDataStore
    @Inject lateinit var songDao: SongDao
    @Inject lateinit var favoriteDao: FavoriteDao
    @Inject lateinit var playlistDao: PlaylistDao
    @Inject lateinit var recentlyPlayedDao: RecentlyPlayedDao

    private var mediaLibrarySession: MediaLibrarySession? = null
    private lateinit var player: ExoPlayer
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        const val ROOT_ID = "root_music"
        const val FOLDER_FAVORITES = "folder_favorites"
        const val FOLDER_PLAYLISTS = "folder_playlists"
        const val FOLDER_RECENT = "folder_recent"
        const val FOLDER_DISCOVER = "folder_discover"
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        becomingNoisyReceiver = BecomingNoisyReceiver(this) {
            player.pause()
        }
        becomingNoisyReceiver.register()

        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.mediaId?.let { songId ->
                    serviceScope.launch(Dispatchers.IO) {
                        recentlyPlayedDao.insertRecentlyPlayed(RecentlyPlayedEntity(songId = songId))
                        preferencesDataStore.saveLastPlayedState(songId, 0L)
                    }
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    initEqualizerEffects()
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (!isPlaying) {
                    player.currentMediaItem?.mediaId?.let { songId ->
                        serviceScope.launch(Dispatchers.IO) {
                            preferencesDataStore.saveLastPlayedState(songId, player.currentPosition)
                        }
                    }
                }
            }
        })

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaLibrarySession = MediaLibrarySession.Builder(this, player, LibrarySessionCallback())
            .setSessionActivity(pendingIntent)
            .build()
    }

    private fun initEqualizerEffects() {
        serviceScope.launch {
            val audioSessionId = player.audioSessionId
            if (audioSessionId != C.AUDIO_SESSION_ID_UNSET && audioSessionId != 0) {
                val config = preferencesDataStore.equalizerConfig.first()
                audioEffectController.initEffects(audioSessionId, config)
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onDestroy() {
        becomingNoisyReceiver.unregister()
        audioEffectController.release()
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        serviceScope.cancel()
        super.onDestroy()
    }

    private inner class LibrarySessionCallback : MediaLibrarySession.Callback {

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val rootItem = MediaItem.Builder()
                .setMediaId(ROOT_ID)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("MyMusic")
                        .setIsBrowsable(true)
                        .setIsPlayable(false)
                        .build()
                )
                .build()
            return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            return when (parentId) {
                ROOT_ID -> {
                    val rootFolders = listOf(
                        createFolderItem(FOLDER_DISCOVER, "Discover & Samples"),
                        createFolderItem(FOLDER_FAVORITES, "Favorites"),
                        createFolderItem(FOLDER_RECENT, "Recently Played"),
                        createFolderItem(FOLDER_PLAYLISTS, "Playlists")
                    )
                    Futures.immediateFuture(
                        LibraryResult.ofItemList(ImmutableList.copyOf(rootFolders), params)
                    )
                }
                FOLDER_DISCOVER -> {
                    val items = SamplePermittedCatalog.sampleSongs.map { song ->
                        createPlayableItem(
                            id = song.id,
                            title = song.title,
                            artist = song.artist,
                            album = song.album,
                            artworkUri = song.artworkUri,
                            mediaUri = song.mediaUri
                        )
                    }
                    Futures.immediateFuture(
                        LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
                    )
                }
                FOLDER_FAVORITES -> {
                    val future = SettableFuture.create<LibraryResult<ImmutableList<MediaItem>>>()
                    serviceScope.launch(Dispatchers.IO) {
                        val favorites = favoriteDao.getFavoriteSongs().first()
                        val items = favorites.map { song ->
                            createPlayableItem(
                                id = song.id,
                                title = song.title,
                                artist = song.artist,
                                album = song.album,
                                artworkUri = song.artworkUri,
                                mediaUri = song.mediaUri
                            )
                        }
                        future.set(LibraryResult.ofItemList(ImmutableList.copyOf(items), params))
                    }
                    future
                }
                FOLDER_RECENT -> {
                    val future = SettableFuture.create<LibraryResult<ImmutableList<MediaItem>>>()
                    serviceScope.launch(Dispatchers.IO) {
                        val recent = recentlyPlayedDao.getRecentlyPlayed(20).first()
                        val items = recent.map { song ->
                            createPlayableItem(
                                id = song.id,
                                title = song.title,
                                artist = song.artist,
                                album = song.album,
                                artworkUri = song.artworkUri,
                                mediaUri = song.mediaUri
                            )
                        }
                        future.set(LibraryResult.ofItemList(ImmutableList.copyOf(items), params))
                    }
                    future
                }
                else -> {
                    Futures.immediateFuture(
                        LibraryResult.ofItemList(ImmutableList.of(), params)
                    )
                }
            }
        }

        override fun onAddMediaItems(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: List<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            val updatedItems = mediaItems.map { item ->
                if (item.localConfiguration == null) {
                    val sample = SamplePermittedCatalog.sampleSongs.find { it.id == item.mediaId }
                    if (sample?.mediaUri != null) {
                        item.buildUpon().setUri(sample.mediaUri).build()
                    } else {
                        item
                    }
                } else {
                    item
                }
            }
            return Futures.immediateFuture(updatedItems)
        }
    }

    private fun createFolderItem(id: String, title: String): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setIsBrowsable(true)
                    .setIsPlayable(false)
                    .build()
            )
            .build()
    }

    private fun createPlayableItem(
        id: String,
        title: String,
        artist: String,
        album: String,
        artworkUri: String?,
        mediaUri: String?
    ): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setAlbumTitle(album)
            .setArtworkUri(artworkUri?.let { Uri.parse(it) })
            .setIsBrowsable(false)
            .setIsPlayable(true)
            .build()

        val builder = MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(metadata)

        if (mediaUri != null) {
            builder.setUri(mediaUri)
        }
        return builder.build()
    }
}
