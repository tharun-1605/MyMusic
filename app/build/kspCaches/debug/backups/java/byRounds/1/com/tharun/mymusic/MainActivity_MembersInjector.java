package com.tharun.mymusic;

import com.tharun.mymusic.data.repository.PlaylistRepositoryImpl;
import com.tharun.mymusic.data.repository.SongRepositoryImpl;
import com.tharun.mymusic.domain.repository.UserPreferencesRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<UserPreferencesRepository> userPreferencesRepositoryProvider;

  private final Provider<SongRepositoryImpl> songRepositoryProvider;

  private final Provider<PlaylistRepositoryImpl> playlistRepositoryProvider;

  public MainActivity_MembersInjector(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider,
      Provider<SongRepositoryImpl> songRepositoryProvider,
      Provider<PlaylistRepositoryImpl> playlistRepositoryProvider) {
    this.userPreferencesRepositoryProvider = userPreferencesRepositoryProvider;
    this.songRepositoryProvider = songRepositoryProvider;
    this.playlistRepositoryProvider = playlistRepositoryProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider,
      Provider<SongRepositoryImpl> songRepositoryProvider,
      Provider<PlaylistRepositoryImpl> playlistRepositoryProvider) {
    return new MainActivity_MembersInjector(userPreferencesRepositoryProvider, songRepositoryProvider, playlistRepositoryProvider);
  }

  public static MembersInjector<MainActivity> create(
      javax.inject.Provider<UserPreferencesRepository> userPreferencesRepositoryProvider,
      javax.inject.Provider<SongRepositoryImpl> songRepositoryProvider,
      javax.inject.Provider<PlaylistRepositoryImpl> playlistRepositoryProvider) {
    return new MainActivity_MembersInjector(Providers.asDaggerProvider(userPreferencesRepositoryProvider), Providers.asDaggerProvider(songRepositoryProvider), Providers.asDaggerProvider(playlistRepositoryProvider));
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectUserPreferencesRepository(instance, userPreferencesRepositoryProvider.get());
    injectSongRepository(instance, songRepositoryProvider.get());
    injectPlaylistRepository(instance, playlistRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.tharun.mymusic.MainActivity.userPreferencesRepository")
  public static void injectUserPreferencesRepository(MainActivity instance,
      UserPreferencesRepository userPreferencesRepository) {
    instance.userPreferencesRepository = userPreferencesRepository;
  }

  @InjectedFieldSignature("com.tharun.mymusic.MainActivity.songRepository")
  public static void injectSongRepository(MainActivity instance,
      SongRepositoryImpl songRepository) {
    instance.songRepository = songRepository;
  }

  @InjectedFieldSignature("com.tharun.mymusic.MainActivity.playlistRepository")
  public static void injectPlaylistRepository(MainActivity instance,
      PlaylistRepositoryImpl playlistRepository) {
    instance.playlistRepository = playlistRepository;
  }
}
