package com.tharun.mymusic.feature.playlists;

import androidx.lifecycle.SavedStateHandle;
import com.tharun.mymusic.domain.repository.FavoriteRepository;
import com.tharun.mymusic.domain.repository.PlaybackRepository;
import com.tharun.mymusic.domain.repository.PlaylistRepository;
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
public final class PlaylistDetailViewModel_Factory implements Factory<PlaylistDetailViewModel> {
  private final Provider<PlaylistRepository> playlistRepositoryProvider;

  private final Provider<PlaybackRepository> playbackRepositoryProvider;

  private final Provider<FavoriteRepository> favoriteRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public PlaylistDetailViewModel_Factory(Provider<PlaylistRepository> playlistRepositoryProvider,
      Provider<PlaybackRepository> playbackRepositoryProvider,
      Provider<FavoriteRepository> favoriteRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.playlistRepositoryProvider = playlistRepositoryProvider;
    this.playbackRepositoryProvider = playbackRepositoryProvider;
    this.favoriteRepositoryProvider = favoriteRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public PlaylistDetailViewModel get() {
    return newInstance(playlistRepositoryProvider.get(), playbackRepositoryProvider.get(), favoriteRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static PlaylistDetailViewModel_Factory create(
      javax.inject.Provider<PlaylistRepository> playlistRepositoryProvider,
      javax.inject.Provider<PlaybackRepository> playbackRepositoryProvider,
      javax.inject.Provider<FavoriteRepository> favoriteRepositoryProvider,
      javax.inject.Provider<SavedStateHandle> savedStateHandleProvider) {
    return new PlaylistDetailViewModel_Factory(Providers.asDaggerProvider(playlistRepositoryProvider), Providers.asDaggerProvider(playbackRepositoryProvider), Providers.asDaggerProvider(favoriteRepositoryProvider), Providers.asDaggerProvider(savedStateHandleProvider));
  }

  public static PlaylistDetailViewModel_Factory create(
      Provider<PlaylistRepository> playlistRepositoryProvider,
      Provider<PlaybackRepository> playbackRepositoryProvider,
      Provider<FavoriteRepository> favoriteRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new PlaylistDetailViewModel_Factory(playlistRepositoryProvider, playbackRepositoryProvider, favoriteRepositoryProvider, savedStateHandleProvider);
  }

  public static PlaylistDetailViewModel newInstance(PlaylistRepository playlistRepository,
      PlaybackRepository playbackRepository, FavoriteRepository favoriteRepository,
      SavedStateHandle savedStateHandle) {
    return new PlaylistDetailViewModel(playlistRepository, playbackRepository, favoriteRepository, savedStateHandle);
  }
}
