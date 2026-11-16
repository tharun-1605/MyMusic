package com.tharun.mymusic.di;

import com.tharun.mymusic.data.local.dao.SongDao;
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
public final class DatabaseModule_ProvideSongDaoFactory implements Factory<SongDao> {
  private final Provider<MyMusicDatabase> databaseProvider;

  public DatabaseModule_ProvideSongDaoFactory(Provider<MyMusicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SongDao get() {
    return provideSongDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideSongDaoFactory create(
      javax.inject.Provider<MyMusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideSongDaoFactory(Providers.asDaggerProvider(databaseProvider));
  }

  public static DatabaseModule_ProvideSongDaoFactory create(
      Provider<MyMusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideSongDaoFactory(databaseProvider);
  }

  public static SongDao provideSongDao(MyMusicDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSongDao(database));
  }
}
