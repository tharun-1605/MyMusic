package com.tharun.mymusic.data.remote.discovery;

import com.tharun.mymusic.data.local.preferences.UserPreferencesDataStore;
import com.tharun.mymusic.data.remote.youtube.YouTubeApiService;
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
public final class YouTubeDiscoveryProvider_Factory implements Factory<YouTubeDiscoveryProvider> {
  private final Provider<YouTubeApiService> youTubeApiServiceProvider;

  private final Provider<UserPreferencesDataStore> preferencesDataStoreProvider;

  public YouTubeDiscoveryProvider_Factory(Provider<YouTubeApiService> youTubeApiServiceProvider,
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider) {
    this.youTubeApiServiceProvider = youTubeApiServiceProvider;
    this.preferencesDataStoreProvider = preferencesDataStoreProvider;
  }

  @Override
  public YouTubeDiscoveryProvider get() {
    return newInstance(youTubeApiServiceProvider.get(), preferencesDataStoreProvider.get());
  }

  public static YouTubeDiscoveryProvider_Factory create(
      javax.inject.Provider<YouTubeApiService> youTubeApiServiceProvider,
      javax.inject.Provider<UserPreferencesDataStore> preferencesDataStoreProvider) {
    return new YouTubeDiscoveryProvider_Factory(Providers.asDaggerProvider(youTubeApiServiceProvider), Providers.asDaggerProvider(preferencesDataStoreProvider));
  }

  public static YouTubeDiscoveryProvider_Factory create(
      Provider<YouTubeApiService> youTubeApiServiceProvider,
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider) {
    return new YouTubeDiscoveryProvider_Factory(youTubeApiServiceProvider, preferencesDataStoreProvider);
  }

  public static YouTubeDiscoveryProvider newInstance(YouTubeApiService youTubeApiService,
      UserPreferencesDataStore preferencesDataStore) {
    return new YouTubeDiscoveryProvider(youTubeApiService, preferencesDataStore);
  }
}
