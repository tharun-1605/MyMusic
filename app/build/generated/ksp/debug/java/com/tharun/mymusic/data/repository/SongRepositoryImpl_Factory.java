package com.tharun.mymusic.data.repository;

import com.tharun.mymusic.core.dispatchers.DispatcherProvider;
import com.tharun.mymusic.data.local.dao.FavoriteDao;
import com.tharun.mymusic.data.local.dao.SongDao;
import com.tharun.mymusic.data.remote.discovery.LocalDeviceDiscoveryProvider;
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
public final class SongRepositoryImpl_Factory implements Factory<SongRepositoryImpl> {
  private final Provider<SongDao> songDaoProvider;

  private final Provider<FavoriteDao> favoriteDaoProvider;

  private final Provider<LocalDeviceDiscoveryProvider> localDeviceDiscoveryProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public SongRepositoryImpl_Factory(Provider<SongDao> songDaoProvider,
      Provider<FavoriteDao> favoriteDaoProvider,
      Provider<LocalDeviceDiscoveryProvider> localDeviceDiscoveryProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.songDaoProvider = songDaoProvider;
    this.favoriteDaoProvider = favoriteDaoProvider;
    this.localDeviceDiscoveryProvider = localDeviceDiscoveryProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public SongRepositoryImpl get() {
    return newInstance(songDaoProvider.get(), favoriteDaoProvider.get(), localDeviceDiscoveryProvider.get(), dispatchersProvider.get());
  }

  public static SongRepositoryImpl_Factory create(javax.inject.Provider<SongDao> songDaoProvider,
      javax.inject.Provider<FavoriteDao> favoriteDaoProvider,
      javax.inject.Provider<LocalDeviceDiscoveryProvider> localDeviceDiscoveryProvider,
      javax.inject.Provider<DispatcherProvider> dispatchersProvider) {
    return new SongRepositoryImpl_Factory(Providers.asDaggerProvider(songDaoProvider), Providers.asDaggerProvider(favoriteDaoProvider), Providers.asDaggerProvider(localDeviceDiscoveryProvider), Providers.asDaggerProvider(dispatchersProvider));
  }

  public static SongRepositoryImpl_Factory create(Provider<SongDao> songDaoProvider,
      Provider<FavoriteDao> favoriteDaoProvider,
      Provider<LocalDeviceDiscoveryProvider> localDeviceDiscoveryProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new SongRepositoryImpl_Factory(songDaoProvider, favoriteDaoProvider, localDeviceDiscoveryProvider, dispatchersProvider);
  }

  public static SongRepositoryImpl newInstance(SongDao songDao, FavoriteDao favoriteDao,
      LocalDeviceDiscoveryProvider localDeviceDiscoveryProvider, DispatcherProvider dispatchers) {
    return new SongRepositoryImpl(songDao, favoriteDao, localDeviceDiscoveryProvider, dispatchers);
  }
}
