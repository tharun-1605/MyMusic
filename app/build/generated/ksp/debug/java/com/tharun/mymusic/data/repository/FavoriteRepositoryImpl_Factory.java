package com.tharun.mymusic.data.repository;

import com.tharun.mymusic.core.dispatchers.DispatcherProvider;
import com.tharun.mymusic.data.local.dao.FavoriteDao;
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
public final class FavoriteRepositoryImpl_Factory implements Factory<FavoriteRepositoryImpl> {
  private final Provider<FavoriteDao> favoriteDaoProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public FavoriteRepositoryImpl_Factory(Provider<FavoriteDao> favoriteDaoProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.favoriteDaoProvider = favoriteDaoProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public FavoriteRepositoryImpl get() {
    return newInstance(favoriteDaoProvider.get(), dispatchersProvider.get());
  }

  public static FavoriteRepositoryImpl_Factory create(
      javax.inject.Provider<FavoriteDao> favoriteDaoProvider,
      javax.inject.Provider<DispatcherProvider> dispatchersProvider) {
    return new FavoriteRepositoryImpl_Factory(Providers.asDaggerProvider(favoriteDaoProvider), Providers.asDaggerProvider(dispatchersProvider));
  }

  public static FavoriteRepositoryImpl_Factory create(Provider<FavoriteDao> favoriteDaoProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new FavoriteRepositoryImpl_Factory(favoriteDaoProvider, dispatchersProvider);
  }

  public static FavoriteRepositoryImpl newInstance(FavoriteDao favoriteDao,
      DispatcherProvider dispatchers) {
    return new FavoriteRepositoryImpl(favoriteDao, dispatchers);
  }
}
