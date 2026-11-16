package com.tharun.mymusic.domain.usecase;

import com.tharun.mymusic.domain.repository.LyricsRepository;
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
public final class GetLyricsUseCase_Factory implements Factory<GetLyricsUseCase> {
  private final Provider<LyricsRepository> lyricsRepositoryProvider;

  public GetLyricsUseCase_Factory(Provider<LyricsRepository> lyricsRepositoryProvider) {
    this.lyricsRepositoryProvider = lyricsRepositoryProvider;
  }

  @Override
  public GetLyricsUseCase get() {
    return newInstance(lyricsRepositoryProvider.get());
  }

  public static GetLyricsUseCase_Factory create(
      javax.inject.Provider<LyricsRepository> lyricsRepositoryProvider) {
    return new GetLyricsUseCase_Factory(Providers.asDaggerProvider(lyricsRepositoryProvider));
  }

  public static GetLyricsUseCase_Factory create(
      Provider<LyricsRepository> lyricsRepositoryProvider) {
    return new GetLyricsUseCase_Factory(lyricsRepositoryProvider);
  }

  public static GetLyricsUseCase newInstance(LyricsRepository lyricsRepository) {
    return new GetLyricsUseCase(lyricsRepository);
  }
}
