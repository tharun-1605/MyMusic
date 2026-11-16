package com.tharun.mymusic.data.remote.discovery;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class LocalDeviceDiscoveryProvider_Factory implements Factory<LocalDeviceDiscoveryProvider> {
  private final Provider<Context> contextProvider;

  public LocalDeviceDiscoveryProvider_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LocalDeviceDiscoveryProvider get() {
    return newInstance(contextProvider.get());
  }

  public static LocalDeviceDiscoveryProvider_Factory create(
      javax.inject.Provider<Context> contextProvider) {
    return new LocalDeviceDiscoveryProvider_Factory(Providers.asDaggerProvider(contextProvider));
  }

  public static LocalDeviceDiscoveryProvider_Factory create(Provider<Context> contextProvider) {
    return new LocalDeviceDiscoveryProvider_Factory(contextProvider);
  }

  public static LocalDeviceDiscoveryProvider newInstance(Context context) {
    return new LocalDeviceDiscoveryProvider(context);
  }
}
