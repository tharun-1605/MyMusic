package com.tharun.mymusic.di;

import com.tharun.mymusic.data.local.dao.PlaylistDao;
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
public final class DatabaseModule_ProvidePlaylistDaoFactory implements Factory<PlaylistDao> {
  private final Provider<MyMusicDatabase> databaseProvider;

  public DatabaseModule_ProvidePlaylistDaoFactory(Provider<MyMusicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public PlaylistDao get() {
    return providePlaylistDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvidePlaylistDaoFactory create(
      javax.inject.Provider<MyMusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvidePlaylistDaoFactory(Providers.asDaggerProvider(databaseProvider));
  }

  public static DatabaseModule_ProvidePlaylistDaoFactory create(
      Provider<MyMusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvidePlaylistDaoFactory(databaseProvider);
  }

  public static PlaylistDao providePlaylistDao(MyMusicDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePlaylistDao(database));
  }
}
