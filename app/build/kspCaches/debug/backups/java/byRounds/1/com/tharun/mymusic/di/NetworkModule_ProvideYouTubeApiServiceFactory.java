package com.tharun.mymusic.di;

import com.tharun.mymusic.data.remote.youtube.YouTubeApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import kotlinx.serialization.json.Json;
import okhttp3.OkHttpClient;

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
public final class NetworkModule_ProvideYouTubeApiServiceFactory implements Factory<YouTubeApiService> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  private final Provider<Json> jsonProvider;

  public NetworkModule_ProvideYouTubeApiServiceFactory(Provider<OkHttpClient> okHttpClientProvider,
      Provider<Json> jsonProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
    this.jsonProvider = jsonProvider;
  }

  @Override
  public YouTubeApiService get() {
    return provideYouTubeApiService(okHttpClientProvider.get(), jsonProvider.get());
  }

  public static NetworkModule_ProvideYouTubeApiServiceFactory create(
      javax.inject.Provider<OkHttpClient> okHttpClientProvider,
      javax.inject.Provider<Json> jsonProvider) {
    return new NetworkModule_ProvideYouTubeApiServiceFactory(Providers.asDaggerProvider(okHttpClientProvider), Providers.asDaggerProvider(jsonProvider));
  }

  public static NetworkModule_ProvideYouTubeApiServiceFactory create(
      Provider<OkHttpClient> okHttpClientProvider, Provider<Json> jsonProvider) {
    return new NetworkModule_ProvideYouTubeApiServiceFactory(okHttpClientProvider, jsonProvider);
  }

  public static YouTubeApiService provideYouTubeApiService(OkHttpClient okHttpClient, Json json) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideYouTubeApiService(okHttpClient, json));
  }
}
