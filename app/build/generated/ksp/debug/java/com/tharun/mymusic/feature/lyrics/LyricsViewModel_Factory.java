package com.tharun.mymusic.feature.lyrics;

import androidx.lifecycle.SavedStateHandle;
import com.tharun.mymusic.domain.repository.PlaybackRepository;
import com.tharun.mymusic.domain.repository.SongRepository;
import com.tharun.mymusic.domain.usecase.GetLyricsUseCase;
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
public final class LyricsViewModel_Factory implements Factory<LyricsViewModel> {
  private final Provider<GetLyricsUseCase> getLyricsUseCaseProvider;

  private final Provider<SongRepository> songRepositoryProvider;

  private final Provider<PlaybackRepository> playbackRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public LyricsViewModel_Factory(Provider<GetLyricsUseCase> getLyricsUseCaseProvider,
      Provider<SongRepository> songRepositoryProvider,
      Provider<PlaybackRepository> playbackRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.getLyricsUseCaseProvider = getLyricsUseCaseProvider;
    this.songRepositoryProvider = songRepositoryProvider;
    this.playbackRepositoryProvider = playbackRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public LyricsViewModel get() {
    return newInstance(getLyricsUseCaseProvider.get(), songRepositoryProvider.get(), playbackRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static LyricsViewModel_Factory create(
      javax.inject.Provider<GetLyricsUseCase> getLyricsUseCaseProvider,
      javax.inject.Provider<SongRepository> songRepositoryProvider,
      javax.inject.Provider<PlaybackRepository> playbackRepositoryProvider,
      javax.inject.Provider<SavedStateHandle> savedStateHandleProvider) {
    return new LyricsViewModel_Factory(Providers.asDaggerProvider(getLyricsUseCaseProvider), Providers.asDaggerProvider(songRepositoryProvider), Providers.asDaggerProvider(playbackRepositoryProvider), Providers.asDaggerProvider(savedStateHandleProvider));
  }

  public static LyricsViewModel_Factory create(Provider<GetLyricsUseCase> getLyricsUseCaseProvider,
      Provider<SongRepository> songRepositoryProvider,
      Provider<PlaybackRepository> playbackRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new LyricsViewModel_Factory(getLyricsUseCaseProvider, songRepositoryProvider, playbackRepositoryProvider, savedStateHandleProvider);
  }

  public static LyricsViewModel newInstance(GetLyricsUseCase getLyricsUseCase,
      SongRepository songRepository, PlaybackRepository playbackRepository,
      SavedStateHandle savedStateHandle) {
    return new LyricsViewModel(getLyricsUseCase, songRepository, playbackRepository, savedStateHandle);
  }
}
