package com.tharun.mymusic.data.repository;

import com.tharun.mymusic.core.dispatchers.DispatcherProvider;
import com.tharun.mymusic.data.remote.discovery.SampleDiscoveryProvider;
import com.tharun.mymusic.data.remote.discovery.YouTubeDiscoveryProvider;
import com.tharun.mymusic.domain.repository.PlaylistRepository;
import com.tharun.mymusic.domain.repository.SongRepository;
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
public final class MusicDiscoveryRepositoryImpl_Factory implements Factory<MusicDiscoveryRepositoryImpl> {
  private final Provider<SampleDiscoveryProvider> sampleDiscoveryProvider;

  private final Provider<YouTubeDiscoveryProvider> youTubeDiscoveryProvider;

  private final Provider<SongRepository> songRepositoryProvider;

  private final Provider<PlaylistRepository> playlistRepositoryProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public MusicDiscoveryRepositoryImpl_Factory(
      Provider<SampleDiscoveryProvider> sampleDiscoveryProvider,
      Provider<YouTubeDiscoveryProvider> youTubeDiscoveryProvider,
      Provider<SongRepository> songRepositoryProvider,
      Provider<PlaylistRepository> playlistRepositoryProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.sampleDiscoveryProvider = sampleDiscoveryProvider;
    this.youTubeDiscoveryProvider = youTubeDiscoveryProvider;
    this.songRepositoryProvider = songRepositoryProvider;
    this.playlistRepositoryProvider = playlistRepositoryProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public MusicDiscoveryRepositoryImpl get() {
    return newInstance(sampleDiscoveryProvider.get(), youTubeDiscoveryProvider.get(), songRepositoryProvider.get(), playlistRepositoryProvider.get(), dispatchersProvider.get());
  }

  public static MusicDiscoveryRepositoryImpl_Factory create(
      javax.inject.Provider<SampleDiscoveryProvider> sampleDiscoveryProvider,
      javax.inject.Provider<YouTubeDiscoveryProvider> youTubeDiscoveryProvider,
      javax.inject.Provider<SongRepository> songRepositoryProvider,
      javax.inject.Provider<PlaylistRepository> playlistRepositoryProvider,
      javax.inject.Provider<DispatcherProvider> dispatchersProvider) {
    return new MusicDiscoveryRepositoryImpl_Factory(Providers.asDaggerProvider(sampleDiscoveryProvider), Providers.asDaggerProvider(youTubeDiscoveryProvider), Providers.asDaggerProvider(songRepositoryProvider), Providers.asDaggerProvider(playlistRepositoryProvider), Providers.asDaggerProvider(dispatchersProvider));
  }

  public static MusicDiscoveryRepositoryImpl_Factory create(
      Provider<SampleDiscoveryProvider> sampleDiscoveryProvider,
      Provider<YouTubeDiscoveryProvider> youTubeDiscoveryProvider,
      Provider<SongRepository> songRepositoryProvider,
      Provider<PlaylistRepository> playlistRepositoryProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new MusicDiscoveryRepositoryImpl_Factory(sampleDiscoveryProvider, youTubeDiscoveryProvider, songRepositoryProvider, playlistRepositoryProvider, dispatchersProvider);
  }

  public static MusicDiscoveryRepositoryImpl newInstance(
      SampleDiscoveryProvider sampleDiscoveryProvider,
      YouTubeDiscoveryProvider youTubeDiscoveryProvider, SongRepository songRepository,
      PlaylistRepository playlistRepository, DispatcherProvider dispatchers) {
    return new MusicDiscoveryRepositoryImpl(sampleDiscoveryProvider, youTubeDiscoveryProvider, songRepository, playlistRepository, dispatchers);
  }
}
