package com.tharun.mymusic.data.repository;

import com.tharun.mymusic.core.dispatchers.DispatcherProvider;
import com.tharun.mymusic.data.local.dao.LyricsDao;
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
public final class LyricsRepositoryImpl_Factory implements Factory<LyricsRepositoryImpl> {
  private final Provider<LyricsDao> lyricsDaoProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public LyricsRepositoryImpl_Factory(Provider<LyricsDao> lyricsDaoProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.lyricsDaoProvider = lyricsDaoProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public LyricsRepositoryImpl get() {
    return newInstance(lyricsDaoProvider.get(), dispatchersProvider.get());
  }

  public static LyricsRepositoryImpl_Factory create(
      javax.inject.Provider<LyricsDao> lyricsDaoProvider,
      javax.inject.Provider<DispatcherProvider> dispatchersProvider) {
    return new LyricsRepositoryImpl_Factory(Providers.asDaggerProvider(lyricsDaoProvider), Providers.asDaggerProvider(dispatchersProvider));
  }

  public static LyricsRepositoryImpl_Factory create(Provider<LyricsDao> lyricsDaoProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new LyricsRepositoryImpl_Factory(lyricsDaoProvider, dispatchersProvider);
  }

  public static LyricsRepositoryImpl newInstance(LyricsDao lyricsDao,
      DispatcherProvider dispatchers) {
    return new LyricsRepositoryImpl(lyricsDao, dispatchers);
  }
}
