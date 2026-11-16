package com.tharun.mymusic.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tharun.mymusic.core.dispatchers.DefaultDispatcherProvider
import com.tharun.mymusic.core.dispatchers.DispatcherProvider
import com.tharun.mymusic.data.local.dao.FavoriteDao
import com.tharun.mymusic.data.local.dao.LyricsDao
import com.tharun.mymusic.data.local.dao.PlaylistDao
import com.tharun.mymusic.data.local.dao.RecentlyPlayedDao
import com.tharun.mymusic.data.local.dao.SearchHistoryDao
import com.tharun.mymusic.data.local.dao.SongDao
import com.tharun.mymusic.data.local.db.MyMusicDatabase
import com.tharun.mymusic.data.remote.youtube.YouTubeApiService
import com.tharun.mymusic.data.repository.FavoriteRepositoryImpl
import com.tharun.mymusic.data.repository.LyricsRepositoryImpl
import com.tharun.mymusic.data.repository.MusicDiscoveryRepositoryImpl
import com.tharun.mymusic.data.repository.PlaybackRepositoryImpl
import com.tharun.mymusic.data.repository.PlaylistRepositoryImpl
import com.tharun.mymusic.data.repository.RecentlyPlayedRepositoryImpl
import com.tharun.mymusic.data.repository.SearchHistoryRepositoryImpl
import com.tharun.mymusic.data.repository.SongRepositoryImpl
import com.tharun.mymusic.data.repository.UserPreferencesRepositoryImpl
import com.tharun.mymusic.domain.repository.FavoriteRepository
import com.tharun.mymusic.domain.repository.LyricsRepository
import com.tharun.mymusic.domain.repository.MusicDiscoveryRepository
import com.tharun.mymusic.domain.repository.PlaybackRepository
import com.tharun.mymusic.domain.repository.PlaylistRepository
import com.tharun.mymusic.domain.repository.RecentlyPlayedRepository
import com.tharun.mymusic.domain.repository.SearchHistoryRepository
import com.tharun.mymusic.domain.repository.SongRepository
import com.tharun.mymusic.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MyMusicDatabase {
        return Room.databaseBuilder(
            context,
            MyMusicDatabase::class.java,
            MyMusicDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSongDao(database: MyMusicDatabase): SongDao = database.songDao()

    @Provides
    fun providePlaylistDao(database: MyMusicDatabase): PlaylistDao = database.playlistDao()

    @Provides
    fun provideFavoriteDao(database: MyMusicDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideRecentlyPlayedDao(database: MyMusicDatabase): RecentlyPlayedDao = database.recentlyPlayedDao()

    @Provides
    fun provideSearchHistoryDao(database: MyMusicDatabase): SearchHistoryDao = database.searchHistoryDao()

    @Provides
    fun provideLyricsDao(database: MyMusicDatabase): LyricsDao = database.lyricsDao()
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideYouTubeApiService(
        okHttpClient: OkHttpClient,
        json: Json
    ): YouTubeApiService {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(YouTubeApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSongRepository(impl: SongRepositoryImpl): SongRepository

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(impl: PlaylistRepositoryImpl): PlaylistRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindRecentlyPlayedRepository(impl: RecentlyPlayedRepositoryImpl): RecentlyPlayedRepository

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(impl: SearchHistoryRepositoryImpl): SearchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindLyricsRepository(impl: LyricsRepositoryImpl): LyricsRepository

    @Binds
    @Singleton
    abstract fun bindMusicDiscoveryRepository(impl: MusicDiscoveryRepositoryImpl): MusicDiscoveryRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindPlaybackRepository(impl: PlaybackRepositoryImpl): PlaybackRepository
}
