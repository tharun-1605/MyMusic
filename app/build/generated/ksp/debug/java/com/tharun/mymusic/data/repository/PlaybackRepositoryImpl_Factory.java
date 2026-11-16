package com.tharun.mymusic.data.repository;

import android.content.Context;
import com.tharun.mymusic.core.dispatchers.DispatcherProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class PlaybackRepositoryImpl_Factory implements Factory<PlaybackRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public PlaybackRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.contextProvider = contextProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public PlaybackRepositoryImpl get() {
    return newInstance(contextProvider.get(), dispatchersProvider.get());
  }

  public static PlaybackRepositoryImpl_Factory create(
      javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<DispatcherProvider> dispatchersProvider) {
    return new PlaybackRepositoryImpl_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(dispatchersProvider));
  }

  public static PlaybackRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new PlaybackRepositoryImpl_Factory(contextProvider, dispatchersProvider);
  }

  public static PlaybackRepositoryImpl newInstance(Context context,
      DispatcherProvider dispatchers) {
    return new PlaybackRepositoryImpl(context, dispatchers);
  }
}
