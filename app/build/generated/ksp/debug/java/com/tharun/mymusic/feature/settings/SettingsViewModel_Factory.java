package com.tharun.mymusic.feature.settings;

import com.tharun.mymusic.data.repository.PlaylistRepositoryImpl;
import com.tharun.mymusic.data.repository.SongRepositoryImpl;
import com.tharun.mymusic.domain.repository.RecentlyPlayedRepository;
import com.tharun.mymusic.domain.repository.SearchHistoryRepository;
import com.tharun.mymusic.domain.repository.UserPreferencesRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<UserPreferencesRepository> preferencesRepositoryProvider;

  private final Provider<SearchHistoryRepository> searchHistoryRepositoryProvider;

  private final Provider<RecentlyPlayedRepository> recentlyPlayedRepositoryProvider;

  private final Provider<SongRepositoryImpl> songRepositoryProvider;

  private final Provider<PlaylistRepositoryImpl> playlistRepositoryProvider;

  public SettingsViewModel_Factory(
      Provider<UserPreferencesRepository> preferencesRepositoryProvider,
      Provider<SearchHistoryRepository> searchHistoryRepositoryProvider,
      Provider<RecentlyPlayedRepository> recentlyPlayedRepositoryProvider,
      Provider<SongRepositoryImpl> songRepositoryProvider,
      Provider<PlaylistRepositoryImpl> playlistRepositoryProvider) {
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
    this.searchHistoryRepositoryProvider = searchHistoryRepositoryProvider;
    this.recentlyPlayedRepositoryProvider = recentlyPlayedRepositoryProvider;
    this.songRepositoryProvider = songRepositoryProvider;
    this.playlistRepositoryProvider = playlistRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(preferencesRepositoryProvider.get(), searchHistoryRepositoryProvider.get(), recentlyPlayedRepositoryProvider.get(), songRepositoryProvider.get(), playlistRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(
      javax.inject.Provider<UserPreferencesRepository> preferencesRepositoryProvider,
      javax.inject.Provider<SearchHistoryRepository> searchHistoryRepositoryProvider,
      javax.inject.Provider<RecentlyPlayedRepository> recentlyPlayedRepositoryProvider,
      javax.inject.Provider<SongRepositoryImpl> songRepositoryProvider,
      javax.inject.Provider<PlaylistRepositoryImpl> playlistRepositoryProvider) {
    return new SettingsViewModel_Factory(Providers.asDaggerProvider(preferencesRepositoryProvider), Providers.asDaggerProvider(searchHistoryRepositoryProvider), Providers.asDaggerProvider(recentlyPlayedRepositoryProvider), Providers.asDaggerProvider(songRepositoryProvider), Providers.asDaggerProvider(playlistRepositoryProvider));
  }

  public static SettingsViewModel_Factory create(
      Provider<UserPreferencesRepository> preferencesRepositoryProvider,
      Provider<SearchHistoryRepository> searchHistoryRepositoryProvider,
      Provider<RecentlyPlayedRepository> recentlyPlayedRepositoryProvider,
      Provider<SongRepositoryImpl> songRepositoryProvider,
      Provider<PlaylistRepositoryImpl> playlistRepositoryProvider) {
    return new SettingsViewModel_Factory(preferencesRepositoryProvider, searchHistoryRepositoryProvider, recentlyPlayedRepositoryProvider, songRepositoryProvider, playlistRepositoryProvider);
  }

  public static SettingsViewModel newInstance(UserPreferencesRepository preferencesRepository,
      SearchHistoryRepository searchHistoryRepository,
      RecentlyPlayedRepository recentlyPlayedRepository, SongRepositoryImpl songRepository,
      PlaylistRepositoryImpl playlistRepository) {
    return new SettingsViewModel(preferencesRepository, searchHistoryRepository, recentlyPlayedRepository, songRepository, playlistRepository);
  }
}
