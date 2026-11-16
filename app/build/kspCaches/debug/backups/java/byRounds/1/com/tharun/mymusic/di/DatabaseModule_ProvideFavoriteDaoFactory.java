package com.tharun.mymusic.di;

import com.tharun.mymusic.data.local.dao.FavoriteDao;
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
public final class DatabaseModule_ProvideFavoriteDaoFactory implements Factory<FavoriteDao> {
  private final Provider<MyMusicDatabase> databaseProvider;

  public DatabaseModule_ProvideFavoriteDaoFactory(Provider<MyMusicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public FavoriteDao get() {
    return provideFavoriteDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideFavoriteDaoFactory create(
      javax.inject.Provider<MyMusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideFavoriteDaoFactory(Providers.asDaggerProvider(databaseProvider));
  }

  public static DatabaseModule_ProvideFavoriteDaoFactory create(
      Provider<MyMusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideFavoriteDaoFactory(databaseProvider);
  }

  public static FavoriteDao provideFavoriteDao(MyMusicDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFavoriteDao(database));
  }
}
