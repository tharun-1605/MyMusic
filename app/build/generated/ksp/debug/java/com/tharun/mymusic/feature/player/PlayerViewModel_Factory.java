package com.tharun.mymusic.feature.player;

import com.tharun.mymusic.domain.repository.FavoriteRepository;
import com.tharun.mymusic.domain.repository.PlaybackRepository;
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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<PlaybackRepository> playbackRepositoryProvider;

  private final Provider<FavoriteRepository> favoriteRepositoryProvider;

  public PlayerViewModel_Factory(Provider<PlaybackRepository> playbackRepositoryProvider,
      Provider<FavoriteRepository> favoriteRepositoryProvider) {
    this.playbackRepositoryProvider = playbackRepositoryProvider;
    this.favoriteRepositoryProvider = favoriteRepositoryProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(playbackRepositoryProvider.get(), favoriteRepositoryProvider.get());
  }

  public static PlayerViewModel_Factory create(
      javax.inject.Provider<PlaybackRepository> playbackRepositoryProvider,
      javax.inject.Provider<FavoriteRepository> favoriteRepositoryProvider) {
    return new PlayerViewModel_Factory(Providers.asDaggerProvider(playbackRepositoryProvider), Providers.asDaggerProvider(favoriteRepositoryProvider));
  }

  public static PlayerViewModel_Factory create(
      Provider<PlaybackRepository> playbackRepositoryProvider,
      Provider<FavoriteRepository> favoriteRepositoryProvider) {
    return new PlayerViewModel_Factory(playbackRepositoryProvider, favoriteRepositoryProvider);
  }

  public static PlayerViewModel newInstance(PlaybackRepository playbackRepository,
      FavoriteRepository favoriteRepository) {
    return new PlayerViewModel(playbackRepository, favoriteRepository);
  }
}
