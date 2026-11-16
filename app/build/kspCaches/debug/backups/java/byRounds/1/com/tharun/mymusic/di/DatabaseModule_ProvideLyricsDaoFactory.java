package com.tharun.mymusic.di;

import com.tharun.mymusic.data.local.dao.LyricsDao;
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
public final class DatabaseModule_ProvideLyricsDaoFactory implements Factory<LyricsDao> {
  private final Provider<MyMusicDatabase> databaseProvider;

  public DatabaseModule_ProvideLyricsDaoFactory(Provider<MyMusicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public LyricsDao get() {
    return provideLyricsDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideLyricsDaoFactory create(
      javax.inject.Provider<MyMusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideLyricsDaoFactory(Providers.asDaggerProvider(databaseProvider));
  }

  public static DatabaseModule_ProvideLyricsDaoFactory create(
      Provider<MyMusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideLyricsDaoFactory(databaseProvider);
  }

  public static LyricsDao provideLyricsDao(MyMusicDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideLyricsDao(database));
  }
}
