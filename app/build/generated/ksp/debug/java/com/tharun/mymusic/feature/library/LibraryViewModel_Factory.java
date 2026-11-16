package com.tharun.mymusic.feature.library;

import com.tharun.mymusic.domain.repository.FavoriteRepository;
import com.tharun.mymusic.domain.repository.PlaybackRepository;
import com.tharun.mymusic.domain.repository.PlaylistRepository;
import com.tharun.mymusic.domain.repository.SongRepository;
import com.tharun.mymusic.domain.usecase.ScanDeviceAudioUseCase;
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
public final class LibraryViewModel_Factory implements Factory<LibraryViewModel> {
  private final Provider<SongRepository> songRepositoryProvider;

  private final Provider<PlaylistRepository> playlistRepositoryProvider;

  private final Provider<FavoriteRepository> favoriteRepositoryProvider;

  private final Provider<PlaybackRepository> playbackRepositoryProvider;

  private final Provider<ScanDeviceAudioUseCase> scanDeviceAudioUseCaseProvider;

  public LibraryViewModel_Factory(Provider<SongRepository> songRepositoryProvider,
      Provider<PlaylistRepository> playlistRepositoryProvider,
      Provider<FavoriteRepository> favoriteRepositoryProvider,
      Provider<PlaybackRepository> playbackRepositoryProvider,
      Provider<ScanDeviceAudioUseCase> scanDeviceAudioUseCaseProvider) {
    this.songRepositoryProvider = songRepositoryProvider;
    this.playlistRepositoryProvider = playlistRepositoryProvider;
    this.favoriteRepositoryProvider = favoriteRepositoryProvider;
    this.playbackRepositoryProvider = playbackRepositoryProvider;
    this.scanDeviceAudioUseCaseProvider = scanDeviceAudioUseCaseProvider;
  }

  @Override
  public LibraryViewModel get() {
    return newInstance(songRepositoryProvider.get(), playlistRepositoryProvider.get(), favoriteRepositoryProvider.get(), playbackRepositoryProvider.get(), scanDeviceAudioUseCaseProvider.get());
  }

  public static LibraryViewModel_Factory create(
      javax.inject.Provider<SongRepository> songRepositoryProvider,
      javax.inject.Provider<PlaylistRepository> playlistRepositoryProvider,
      javax.inject.Provider<FavoriteRepository> favoriteRepositoryProvider,
      javax.inject.Provider<PlaybackRepository> playbackRepositoryProvider,
      javax.inject.Provider<ScanDeviceAudioUseCase> scanDeviceAudioUseCaseProvider) {
    return new LibraryViewModel_Factory(Providers.asDaggerProvider(songRepositoryProvider), Providers.asDaggerProvider(playlistRepositoryProvider), Providers.asDaggerProvider(favoriteRepositoryProvider), Providers.asDaggerProvider(playbackRepositoryProvider), Providers.asDaggerProvider(scanDeviceAudioUseCaseProvider));
  }

  public static LibraryViewModel_Factory create(Provider<SongRepository> songRepositoryProvider,
      Provider<PlaylistRepository> playlistRepositoryProvider,
      Provider<FavoriteRepository> favoriteRepositoryProvider,
      Provider<PlaybackRepository> playbackRepositoryProvider,
      Provider<ScanDeviceAudioUseCase> scanDeviceAudioUseCaseProvider) {
    return new LibraryViewModel_Factory(songRepositoryProvider, playlistRepositoryProvider, favoriteRepositoryProvider, playbackRepositoryProvider, scanDeviceAudioUseCaseProvider);
  }

  public static LibraryViewModel newInstance(SongRepository songRepository,
      PlaylistRepository playlistRepository, FavoriteRepository favoriteRepository,
      PlaybackRepository playbackRepository, ScanDeviceAudioUseCase scanDeviceAudioUseCase) {
    return new LibraryViewModel(songRepository, playlistRepository, favoriteRepository, playbackRepository, scanDeviceAudioUseCase);
  }
}
