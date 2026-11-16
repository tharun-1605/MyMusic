package com.tharun.mymusic.data.remote.discovery;

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
public final class SampleDiscoveryProvider_Factory implements Factory<SampleDiscoveryProvider> {
  @Override
  public SampleDiscoveryProvider get() {
    return newInstance();
  }

  public static SampleDiscoveryProvider_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SampleDiscoveryProvider newInstance() {
    return new SampleDiscoveryProvider();
  }

  private static final class InstanceHolder {
    static final SampleDiscoveryProvider_Factory INSTANCE = new SampleDiscoveryProvider_Factory();
  }
}
