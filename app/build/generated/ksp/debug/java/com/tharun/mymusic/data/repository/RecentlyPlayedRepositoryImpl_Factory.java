package com.tharun.mymusic.data.repository;

import com.tharun.mymusic.core.dispatchers.DispatcherProvider;
import com.tharun.mymusic.data.local.dao.FavoriteDao;
import com.tharun.mymusic.data.local.dao.RecentlyPlayedDao;
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
public final class RecentlyPlayedRepositoryImpl_Factory implements Factory<RecentlyPlayedRepositoryImpl> {
  private final Provider<RecentlyPlayedDao> recentlyPlayedDaoProvider;

  private final Provider<FavoriteDao> favoriteDaoProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public RecentlyPlayedRepositoryImpl_Factory(Provider<RecentlyPlayedDao> recentlyPlayedDaoProvider,
      Provider<FavoriteDao> favoriteDaoProvider, Provider<DispatcherProvider> dispatchersProvider) {
    this.recentlyPlayedDaoProvider = recentlyPlayedDaoProvider;
    this.favoriteDaoProvider = favoriteDaoProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public RecentlyPlayedRepositoryImpl get() {
    return newInstance(recentlyPlayedDaoProvider.get(), favoriteDaoProvider.get(), dispatchersProvider.get());
  }

  public static RecentlyPlayedRepositoryImpl_Factory create(
      javax.inject.Provider<RecentlyPlayedDao> recentlyPlayedDaoProvider,
      javax.inject.Provider<FavoriteDao> favoriteDaoProvider,
      javax.inject.Provider<DispatcherProvider> dispatchersProvider) {
    return new RecentlyPlayedRepositoryImpl_Factory(Providers.asDaggerProvider(recentlyPlayedDaoProvider), Providers.asDaggerProvider(favoriteDaoProvider), Providers.asDaggerProvider(dispatchersProvider));
  }

  public static RecentlyPlayedRepositoryImpl_Factory create(
      Provider<RecentlyPlayedDao> recentlyPlayedDaoProvider,
      Provider<FavoriteDao> favoriteDaoProvider, Provider<DispatcherProvider> dispatchersProvider) {
    return new RecentlyPlayedRepositoryImpl_Factory(recentlyPlayedDaoProvider, favoriteDaoProvider, dispatchersProvider);
  }

  public static RecentlyPlayedRepositoryImpl newInstance(RecentlyPlayedDao recentlyPlayedDao,
      FavoriteDao favoriteDao, DispatcherProvider dispatchers) {
    return new RecentlyPlayedRepositoryImpl(recentlyPlayedDao, favoriteDao, dispatchers);
  }
}
