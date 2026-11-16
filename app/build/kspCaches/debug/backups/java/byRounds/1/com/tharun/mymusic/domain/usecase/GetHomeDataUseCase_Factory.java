package com.tharun.mymusic.domain.usecase;

import com.tharun.mymusic.domain.repository.FavoriteRepository;
import com.tharun.mymusic.domain.repository.MusicDiscoveryRepository;
import com.tharun.mymusic.domain.repository.RecentlyPlayedRepository;
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
public final class GetHomeDataUseCase_Factory implements Factory<GetHomeDataUseCase> {
  private final Provider<RecentlyPlayedRepository> recentlyPlayedRepositoryProvider;

  private final Provider<MusicDiscoveryRepository> musicDiscoveryRepositoryProvider;

  private final Provider<FavoriteRepository> favoriteRepositoryProvider;

  public GetHomeDataUseCase_Factory(
      Provider<RecentlyPlayedRepository> recentlyPlayedRepositoryProvider,
      Provider<MusicDiscoveryRepository> musicDiscoveryRepositoryProvider,
      Provider<FavoriteRepository> favoriteRepositoryProvider) {
    this.recentlyPlayedRepositoryProvider = recentlyPlayedRepositoryProvider;
    this.musicDiscoveryRepositoryProvider = musicDiscoveryRepositoryProvider;
    this.favoriteRepositoryProvider = favoriteRepositoryProvider;
  }

  @Override
  public GetHomeDataUseCase get() {
    return newInstance(recentlyPlayedRepositoryProvider.get(), musicDiscoveryRepositoryProvider.get(), favoriteRepositoryProvider.get());
  }

  public static GetHomeDataUseCase_Factory create(
      javax.inject.Provider<RecentlyPlayedRepository> recentlyPlayedRepositoryProvider,
      javax.inject.Provider<MusicDiscoveryRepository> musicDiscoveryRepositoryProvider,
      javax.inject.Provider<FavoriteRepository> favoriteRepositoryProvider) {
    return new GetHomeDataUseCase_Factory(Providers.asDaggerProvider(recentlyPlayedRepositoryProvider), Providers.asDaggerProvider(musicDiscoveryRepositoryProvider), Providers.asDaggerProvider(favoriteRepositoryProvider));
  }

  public static GetHomeDataUseCase_Factory create(
      Provider<RecentlyPlayedRepository> recentlyPlayedRepositoryProvider,
      Provider<MusicDiscoveryRepository> musicDiscoveryRepositoryProvider,
      Provider<FavoriteRepository> favoriteRepositoryProvider) {
    return new GetHomeDataUseCase_Factory(recentlyPlayedRepositoryProvider, musicDiscoveryRepositoryProvider, favoriteRepositoryProvider);
  }

  public static GetHomeDataUseCase newInstance(RecentlyPlayedRepository recentlyPlayedRepository,
      MusicDiscoveryRepository musicDiscoveryRepository, FavoriteRepository favoriteRepository) {
    return new GetHomeDataUseCase(recentlyPlayedRepository, musicDiscoveryRepository, favoriteRepository);
  }
}
