package com.tharun.mymusic.feature.equalizer;

import com.tharun.mymusic.domain.repository.UserPreferencesRepository;
import com.tharun.mymusic.service.AudioEffectController;
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
public final class EqualizerViewModel_Factory implements Factory<EqualizerViewModel> {
  private final Provider<UserPreferencesRepository> preferencesRepositoryProvider;

  private final Provider<AudioEffectController> audioEffectControllerProvider;

  public EqualizerViewModel_Factory(
      Provider<UserPreferencesRepository> preferencesRepositoryProvider,
      Provider<AudioEffectController> audioEffectControllerProvider) {
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
    this.audioEffectControllerProvider = audioEffectControllerProvider;
  }

  @Override
  public EqualizerViewModel get() {
    return newInstance(preferencesRepositoryProvider.get(), audioEffectControllerProvider.get());
  }

  public static EqualizerViewModel_Factory create(
      javax.inject.Provider<UserPreferencesRepository> preferencesRepositoryProvider,
      javax.inject.Provider<AudioEffectController> audioEffectControllerProvider) {
    return new EqualizerViewModel_Factory(Providers.asDaggerProvider(preferencesRepositoryProvider), Providers.asDaggerProvider(audioEffectControllerProvider));
  }

  public static EqualizerViewModel_Factory create(
      Provider<UserPreferencesRepository> preferencesRepositoryProvider,
      Provider<AudioEffectController> audioEffectControllerProvider) {
    return new EqualizerViewModel_Factory(preferencesRepositoryProvider, audioEffectControllerProvider);
  }

  public static EqualizerViewModel newInstance(UserPreferencesRepository preferencesRepository,
      AudioEffectController audioEffectController) {
    return new EqualizerViewModel(preferencesRepository, audioEffectController);
  }
}
