package com.tharun.mymusic.data.repository;

import com.tharun.mymusic.core.dispatchers.DispatcherProvider;
import com.tharun.mymusic.data.local.dao.SearchHistoryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SearchHistoryRepositoryImpl_Factory implements Factory<SearchHistoryRepositoryImpl> {
  private final Provider<SearchHistoryDao> searchHistoryDaoProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  public SearchHistoryRepositoryImpl_Factory(Provider<SearchHistoryDao> searchHistoryDaoProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.searchHistoryDaoProvider = searchHistoryDaoProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public SearchHistoryRepositoryImpl get() {
    return newInstance(searchHistoryDaoProvider.get(), dispatchersProvider.get());
  }

  public static SearchHistoryRepositoryImpl_Factory create(
      javax.inject.Provider<SearchHistoryDao> searchHistoryDaoProvider,
      javax.inject.Provider<DispatcherProvider> dispatchersProvider) {
    return new SearchHistoryRepositoryImpl_Factory(Providers.asDaggerProvider(searchHistoryDaoProvider), Providers.asDaggerProvider(dispatchersProvider));
  }

  public static SearchHistoryRepositoryImpl_Factory create(
      Provider<SearchHistoryDao> searchHistoryDaoProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new SearchHistoryRepositoryImpl_Factory(searchHistoryDaoProvider, dispatchersProvider);
  }

  public static SearchHistoryRepositoryImpl newInstance(SearchHistoryDao searchHistoryDao,
      DispatcherProvider dispatchers) {
    return new SearchHistoryRepositoryImpl(searchHistoryDao, dispatchers);
  }
}
