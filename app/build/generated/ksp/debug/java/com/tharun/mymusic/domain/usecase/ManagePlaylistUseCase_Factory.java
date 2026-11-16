package com.tharun.mymusic.domain.usecase;

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
public final class ManagePlaylistUseCase_Factory implements Factory<ManagePlaylistUseCase> {
  private final Provider<PlaylistRepository> playlistRepositoryProvider;

  public ManagePlaylistUseCase_Factory(Provider<PlaylistRepository> playlistRepositoryProvider) {
    this.playlistRepositoryProvider = playlistRepositoryProvider;
  }

  @Override
  public ManagePlaylistUseCase get() {
    return newInstance(playlistRepositoryProvider.get());
  }

  public static ManagePlaylistUseCase_Factory create(
      javax.inject.Provider<PlaylistRepository> playlistRepositoryProvider) {
    return new ManagePlaylistUseCase_Factory(Providers.asDaggerProvider(playlistRepositoryProvider));
  }

  public static ManagePlaylistUseCase_Factory create(
      Provider<PlaylistRepository> playlistRepositoryProvider) {
    return new ManagePlaylistUseCase_Factory(playlistRepositoryProvider);
  }

  public static ManagePlaylistUseCase newInstance(PlaylistRepository playlistRepository) {
    return new ManagePlaylistUseCase(playlistRepository);
  }
}
