package com.tharun.mymusic.domain.usecase;

import com.tharun.mymusic.domain.repository.SongRepository;
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
public final class ScanDeviceAudioUseCase_Factory implements Factory<ScanDeviceAudioUseCase> {
  private final Provider<SongRepository> songRepositoryProvider;

  public ScanDeviceAudioUseCase_Factory(Provider<SongRepository> songRepositoryProvider) {
    this.songRepositoryProvider = songRepositoryProvider;
  }

  @Override
  public ScanDeviceAudioUseCase get() {
    return newInstance(songRepositoryProvider.get());
  }

  public static ScanDeviceAudioUseCase_Factory create(
      javax.inject.Provider<SongRepository> songRepositoryProvider) {
    return new ScanDeviceAudioUseCase_Factory(Providers.asDaggerProvider(songRepositoryProvider));
  }

  public static ScanDeviceAudioUseCase_Factory create(
      Provider<SongRepository> songRepositoryProvider) {
    return new ScanDeviceAudioUseCase_Factory(songRepositoryProvider);
  }

  public static ScanDeviceAudioUseCase newInstance(SongRepository songRepository) {
    return new ScanDeviceAudioUseCase(songRepository);
  }
}
