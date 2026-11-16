package com.tharun.mymusic.data.repository;

import com.tharun.mymusic.data.local.preferences.UserPreferencesDataStore;
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
public final class UserPreferencesRepositoryImpl_Factory implements Factory<UserPreferencesRepositoryImpl> {
  private final Provider<UserPreferencesDataStore> preferencesDataStoreProvider;

  public UserPreferencesRepositoryImpl_Factory(
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider) {
    this.preferencesDataStoreProvider = preferencesDataStoreProvider;
  }

  @Override
  public UserPreferencesRepositoryImpl get() {
    return newInstance(preferencesDataStoreProvider.get());
  }

  public static UserPreferencesRepositoryImpl_Factory create(
      javax.inject.Provider<UserPreferencesDataStore> preferencesDataStoreProvider) {
    return new UserPreferencesRepositoryImpl_Factory(Providers.asDaggerProvider(preferencesDataStoreProvider));
  }

  public static UserPreferencesRepositoryImpl_Factory create(
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider) {
    return new UserPreferencesRepositoryImpl_Factory(preferencesDataStoreProvider);
  }

  public static UserPreferencesRepositoryImpl newInstance(
      UserPreferencesDataStore preferencesDataStore) {
    return new UserPreferencesRepositoryImpl(preferencesDataStore);
  }
}
