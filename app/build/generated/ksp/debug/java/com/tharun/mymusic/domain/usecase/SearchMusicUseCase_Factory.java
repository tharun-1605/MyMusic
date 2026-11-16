package com.tharun.mymusic.domain.usecase;

import com.tharun.mymusic.domain.repository.MusicDiscoveryRepository;
import com.tharun.mymusic.domain.repository.SongRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class SearchMusicUseCase_Factory implements Factory<SearchMusicUseCase> {
  private final Provider<MusicDiscoveryRepository> discoveryRepositoryProvider;

  private final Provider<SongRepository> songRepositoryProvider;

  public SearchMusicUseCase_Factory(Provider<MusicDiscoveryRepository> discoveryRepositoryProvider,
      Provider<SongRepository> songRepositoryProvider) {
    this.discoveryRepositoryProvider = discoveryRepositoryProvider;
    this.songRepositoryProvider = songRepositoryProvider;
  }

  @Override
  public SearchMusicUseCase get() {
    return newInstance(discoveryRepositoryProvider.get(), songRepositoryProvider.get());
  }

  public static SearchMusicUseCase_Factory create(
      javax.inject.Provider<MusicDiscoveryRepository> discoveryRepositoryProvider,
      javax.inject.Provider<SongRepository> songRepositoryProvider) {
    return new SearchMusicUseCase_Factory(Providers.asDaggerProvider(discoveryRepositoryProvider), Providers.asDaggerProvider(songRepositoryProvider));
  }

  public static SearchMusicUseCase_Factory create(
      Provider<MusicDiscoveryRepository> discoveryRepositoryProvider,
      Provider<SongRepository> songRepositoryProvider) {
    return new SearchMusicUseCase_Factory(discoveryRepositoryProvider, songRepositoryProvider);
  }

  public static SearchMusicUseCase newInstance(MusicDiscoveryRepository discoveryRepository,
      SongRepository songRepository) {
    return new SearchMusicUseCase(discoveryRepository, songRepository);
  }
}
