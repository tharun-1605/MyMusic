package com.tharun.mymusic.di;

import com.tharun.mymusic.data.local.dao.SearchHistoryDao;
import com.tharun.mymusic.data.local.db.MyMusicDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideSearchHistoryDaoFactory implements Factory<SearchHistoryDao> {
  private final Provider<MyMusicDatabase> databaseProvider;

  public DatabaseModule_ProvideSearchHistoryDaoFactory(Provider<MyMusicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SearchHistoryDao get() {
    return provideSearchHistoryDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideSearchHistoryDaoFactory create(
      javax.inject.Provider<MyMusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideSearchHistoryDaoFactory(Providers.asDaggerProvider(databaseProvider));
  }

  public static DatabaseModule_ProvideSearchHistoryDaoFactory create(
      Provider<MyMusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideSearchHistoryDaoFactory(databaseProvider);
  }

  public static SearchHistoryDao provideSearchHistoryDao(MyMusicDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSearchHistoryDao(database));
  }
}
