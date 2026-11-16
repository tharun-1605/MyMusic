package com.tharun.mymusic.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AudioEffectController_Factory implements Factory<AudioEffectController> {
  @Override
  public AudioEffectController get() {
    return newInstance();
  }

  public static AudioEffectController_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AudioEffectController newInstance() {
    return new AudioEffectController();
  }

  private static final class InstanceHolder {
    static final AudioEffectController_Factory INSTANCE = new AudioEffectController_Factory();
  }
}
