package com.tharun.mymusic.feature.home;

import com.tharun.mymusic.domain.repository.FavoriteRepository;
import com.tharun.mymusic.domain.repository.PlaybackRepository;
import com.tharun.mymusic.domain.repository.PlaylistRepository;
import com.tharun.mymusic.domain.usecase.GetHomeDataUseCase;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GetHomeDataUseCase> getHomeDataUseCaseProvider;

  private final Provider<PlaybackRepository> playbackRepositoryProvider;

  private final Provider<FavoriteRepository> favoriteRepositoryProvider;

  private final Provider<PlaylistRepository> playlistRepositoryProvider;

  public HomeViewModel_Factory(Provider<GetHomeDataUseCase> getHomeDataUseCaseProvider,
      Provider<PlaybackRepository> playbackRepositoryProvider,
      Provider<FavoriteRepository> favoriteRepositoryProvider,
      Provider<PlaylistRepository> playlistRepositoryProvider) {
    this.getHomeDataUseCaseProvider = getHomeDataUseCaseProvider;
    this.playbackRepositoryProvider = playbackRepositoryProvider;
    this.favoriteRepositoryProvider = favoriteRepositoryProvider;
    this.playlistRepositoryProvider = playlistRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getHomeDataUseCaseProvider.get(), playbackRepositoryProvider.get(), favoriteRepositoryProvider.get(), playlistRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(
      javax.inject.Provider<GetHomeDataUseCase> getHomeDataUseCaseProvider,
      javax.inject.Provider<PlaybackRepository> playbackRepositoryProvider,
      javax.inject.Provider<FavoriteRepository> favoriteRepositoryProvider,
      javax.inject.Provider<PlaylistRepository> playlistRepositoryProvider) {
    return new HomeViewModel_Factory(Providers.asDaggerProvider(getHomeDataUseCaseProvider), Providers.asDaggerProvider(playbackRepositoryProvider), Providers.asDaggerProvider(favoriteRepositoryProvider), Providers.asDaggerProvider(playlistRepositoryProvider));
  }

  public static HomeViewModel_Factory create(
      Provider<GetHomeDataUseCase> getHomeDataUseCaseProvider,
      Provider<PlaybackRepository> playbackRepositoryProvider,
      Provider<FavoriteRepository> favoriteRepositoryProvider,
      Provider<PlaylistRepository> playlistRepositoryProvider) {
    return new HomeViewModel_Factory(getHomeDataUseCaseProvider, playbackRepositoryProvider, favoriteRepositoryProvider, playlistRepositoryProvider);
  }

  public static HomeViewModel newInstance(GetHomeDataUseCase getHomeDataUseCase,
      PlaybackRepository playbackRepository, FavoriteRepository favoriteRepository,
      PlaylistRepository playlistRepository) {
    return new HomeViewModel(getHomeDataUseCase, playbackRepository, favoriteRepository, playlistRepository);
  }
}
