package com.tharun.mymusic.di;

import com.tharun.mymusic.data.local.dao.RecentlyPlayedDao;
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
public final class DatabaseModule_ProvideRecentlyPlayedDaoFactory implements Factory<RecentlyPlayedDao> {
  private final Provider<MyMusicDatabase> databaseProvider;

  public DatabaseModule_ProvideRecentlyPlayedDaoFactory(
      Provider<MyMusicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public RecentlyPlayedDao get() {
    return provideRecentlyPlayedDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideRecentlyPlayedDaoFactory create(
      javax.inject.Provider<MyMusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideRecentlyPlayedDaoFactory(Providers.asDaggerProvider(databaseProvider));
  }

  public static DatabaseModule_ProvideRecentlyPlayedDaoFactory create(
      Provider<MyMusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideRecentlyPlayedDaoFactory(databaseProvider);
  }

  public static RecentlyPlayedDao provideRecentlyPlayedDao(MyMusicDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRecentlyPlayedDao(database));
  }
}
