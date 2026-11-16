package com.tharun.mymusic.feature.search;

import com.tharun.mymusic.domain.repository.FavoriteRepository;
import com.tharun.mymusic.domain.repository.PlaybackRepository;
import com.tharun.mymusic.domain.repository.SearchHistoryRepository;
import com.tharun.mymusic.domain.usecase.SearchMusicUseCase;
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<SearchMusicUseCase> searchMusicUseCaseProvider;

  private final Provider<SearchHistoryRepository> searchHistoryRepositoryProvider;

  private final Provider<PlaybackRepository> playbackRepositoryProvider;

  private final Provider<FavoriteRepository> favoriteRepositoryProvider;

  public SearchViewModel_Factory(Provider<SearchMusicUseCase> searchMusicUseCaseProvider,
      Provider<SearchHistoryRepository> searchHistoryRepositoryProvider,
      Provider<PlaybackRepository> playbackRepositoryProvider,
      Provider<FavoriteRepository> favoriteRepositoryProvider) {
    this.searchMusicUseCaseProvider = searchMusicUseCaseProvider;
    this.searchHistoryRepositoryProvider = searchHistoryRepositoryProvider;
    this.playbackRepositoryProvider = playbackRepositoryProvider;
    this.favoriteRepositoryProvider = favoriteRepositoryProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(searchMusicUseCaseProvider.get(), searchHistoryRepositoryProvider.get(), playbackRepositoryProvider.get(), favoriteRepositoryProvider.get());
  }

  public static SearchViewModel_Factory create(
      javax.inject.Provider<SearchMusicUseCase> searchMusicUseCaseProvider,
      javax.inject.Provider<SearchHistoryRepository> searchHistoryRepositoryProvider,
      javax.inject.Provider<PlaybackRepository> playbackRepositoryProvider,
      javax.inject.Provider<FavoriteRepository> favoriteRepositoryProvider) {
    return new SearchViewModel_Factory(Providers.asDaggerProvider(searchMusicUseCaseProvider), Providers.asDaggerProvider(searchHistoryRepositoryProvider), Providers.asDaggerProvider(playbackRepositoryProvider), Providers.asDaggerProvider(favoriteRepositoryProvider));
  }

  public static SearchViewModel_Factory create(
      Provider<SearchMusicUseCase> searchMusicUseCaseProvider,
      Provider<SearchHistoryRepository> searchHistoryRepositoryProvider,
      Provider<PlaybackRepository> playbackRepositoryProvider,
      Provider<FavoriteRepository> favoriteRepositoryProvider) {
    return new SearchViewModel_Factory(searchMusicUseCaseProvider, searchHistoryRepositoryProvider, playbackRepositoryProvider, favoriteRepositoryProvider);
  }

  public static SearchViewModel newInstance(SearchMusicUseCase searchMusicUseCase,
      SearchHistoryRepository searchHistoryRepository, PlaybackRepository playbackRepository,
      FavoriteRepository favoriteRepository) {
    return new SearchViewModel(searchMusicUseCase, searchHistoryRepository, playbackRepository, favoriteRepository);
  }
}
