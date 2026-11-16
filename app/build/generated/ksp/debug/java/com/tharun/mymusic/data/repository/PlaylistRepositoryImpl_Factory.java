package com.tharun.mymusic.data.repository;

import com.tharun.mymusic.core.dispatchers.DispatcherProvider;
import com.tharun.mymusic.data.local.dao.FavoriteDao;
import com.tharun.mymusic.data.local.dao.PlaylistDao;
import com.tharun.mymusic.data.local.dao.SongDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class PlaylistRepositoryImpl_Factory implements Factory<PlaylistRepositoryImpl> {
  private final Provider<PlaylistDao> playlistDaoProvider;

  private final Provider<SongDao> songDaoProvider;

  private final Provider<FavoriteDao> favoriteDaoProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public PlaylistRepositoryImpl_Factory(Provider<PlaylistDao> playlistDaoProvider,
      Provider<SongDao> songDaoProvider, Provider<FavoriteDao> favoriteDaoProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.playlistDaoProvider = playlistDaoProvider;
    this.songDaoProvider = songDaoProvider;
    this.favoriteDaoProvider = favoriteDaoProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public PlaylistRepositoryImpl get() {
    return newInstance(playlistDaoProvider.get(), songDaoProvider.get(), favoriteDaoProvider.get(), dispatchersProvider.get());
  }

  public static PlaylistRepositoryImpl_Factory create(
      javax.inject.Provider<PlaylistDao> playlistDaoProvider,
      javax.inject.Provider<SongDao> songDaoProvider,
      javax.inject.Provider<FavoriteDao> favoriteDaoProvider,
      javax.inject.Provider<DispatcherProvider> dispatchersProvider) {
    return new PlaylistRepositoryImpl_Factory(Providers.asDaggerProvider(playlistDaoProvider), Providers.asDaggerProvider(songDaoProvider), Providers.asDaggerProvider(favoriteDaoProvider), Providers.asDaggerProvider(dispatchersProvider));
  }

  public static PlaylistRepositoryImpl_Factory create(Provider<PlaylistDao> playlistDaoProvider,
      Provider<SongDao> songDaoProvider, Provider<FavoriteDao> favoriteDaoProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new PlaylistRepositoryImpl_Factory(playlistDaoProvider, songDaoProvider, favoriteDaoProvider, dispatchersProvider);
  }

  public static PlaylistRepositoryImpl newInstance(PlaylistDao playlistDao, SongDao songDao,
      FavoriteDao favoriteDao, DispatcherProvider dispatchers) {
    return new PlaylistRepositoryImpl(playlistDao, songDao, favoriteDao, dispatchers);
  }
}
