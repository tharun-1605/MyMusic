package com.tharun.mymusic;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.tharun.mymusic.core.dispatchers.DispatcherProvider;
import com.tharun.mymusic.data.local.dao.FavoriteDao;
import com.tharun.mymusic.data.local.dao.LyricsDao;
import com.tharun.mymusic.data.local.dao.PlaylistDao;
import com.tharun.mymusic.data.local.dao.RecentlyPlayedDao;
import com.tharun.mymusic.data.local.dao.SearchHistoryDao;
import com.tharun.mymusic.data.local.dao.SongDao;
import com.tharun.mymusic.data.local.db.MyMusicDatabase;
import com.tharun.mymusic.data.local.preferences.UserPreferencesDataStore;
import com.tharun.mymusic.data.remote.discovery.LocalDeviceDiscoveryProvider;
import com.tharun.mymusic.data.remote.discovery.SampleDiscoveryProvider;
import com.tharun.mymusic.data.remote.discovery.YouTubeDiscoveryProvider;
import com.tharun.mymusic.data.remote.youtube.YouTubeApiService;
import com.tharun.mymusic.data.repository.FavoriteRepositoryImpl;
import com.tharun.mymusic.data.repository.LyricsRepositoryImpl;
import com.tharun.mymusic.data.repository.MusicDiscoveryRepositoryImpl;
import com.tharun.mymusic.data.repository.PlaybackRepositoryImpl;
import com.tharun.mymusic.data.repository.PlaylistRepositoryImpl;
import com.tharun.mymusic.data.repository.RecentlyPlayedRepositoryImpl;
import com.tharun.mymusic.data.repository.SearchHistoryRepositoryImpl;
import com.tharun.mymusic.data.repository.SongRepositoryImpl;
import com.tharun.mymusic.data.repository.UserPreferencesRepositoryImpl;
import com.tharun.mymusic.di.DatabaseModule_ProvideDatabaseFactory;
import com.tharun.mymusic.di.DatabaseModule_ProvideFavoriteDaoFactory;
import com.tharun.mymusic.di.DatabaseModule_ProvideLyricsDaoFactory;
import com.tharun.mymusic.di.DatabaseModule_ProvidePlaylistDaoFactory;
import com.tharun.mymusic.di.DatabaseModule_ProvideRecentlyPlayedDaoFactory;
import com.tharun.mymusic.di.DatabaseModule_ProvideSearchHistoryDaoFactory;
import com.tharun.mymusic.di.DatabaseModule_ProvideSongDaoFactory;
import com.tharun.mymusic.di.DispatchersModule_ProvideDispatcherProviderFactory;
import com.tharun.mymusic.di.NetworkModule_ProvideJsonFactory;
import com.tharun.mymusic.di.NetworkModule_ProvideOkHttpClientFactory;
import com.tharun.mymusic.di.NetworkModule_ProvideYouTubeApiServiceFactory;
import com.tharun.mymusic.domain.usecase.GetHomeDataUseCase;
import com.tharun.mymusic.domain.usecase.GetLyricsUseCase;
import com.tharun.mymusic.domain.usecase.ScanDeviceAudioUseCase;
import com.tharun.mymusic.domain.usecase.SearchMusicUseCase;
import com.tharun.mymusic.feature.equalizer.EqualizerViewModel;
import com.tharun.mymusic.feature.equalizer.EqualizerViewModel_HiltModules;
import com.tharun.mymusic.feature.equalizer.EqualizerViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.tharun.mymusic.feature.equalizer.EqualizerViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.tharun.mymusic.feature.home.HomeViewModel;
import com.tharun.mymusic.feature.home.HomeViewModel_HiltModules;
import com.tharun.mymusic.feature.home.HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.tharun.mymusic.feature.home.HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.tharun.mymusic.feature.library.LibraryViewModel;
import com.tharun.mymusic.feature.library.LibraryViewModel_HiltModules;
import com.tharun.mymusic.feature.library.LibraryViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.tharun.mymusic.feature.library.LibraryViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.tharun.mymusic.feature.lyrics.LyricsViewModel;
import com.tharun.mymusic.feature.lyrics.LyricsViewModel_HiltModules;
import com.tharun.mymusic.feature.lyrics.LyricsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.tharun.mymusic.feature.lyrics.LyricsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.tharun.mymusic.feature.player.PlayerViewModel;
import com.tharun.mymusic.feature.player.PlayerViewModel_HiltModules;
import com.tharun.mymusic.feature.player.PlayerViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.tharun.mymusic.feature.player.PlayerViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.tharun.mymusic.feature.playlists.PlaylistDetailViewModel;
import com.tharun.mymusic.feature.playlists.PlaylistDetailViewModel_HiltModules;
import com.tharun.mymusic.feature.playlists.PlaylistDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.tharun.mymusic.feature.playlists.PlaylistDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.tharun.mymusic.feature.search.SearchViewModel;
import com.tharun.mymusic.feature.search.SearchViewModel_HiltModules;
import com.tharun.mymusic.feature.search.SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.tharun.mymusic.feature.search.SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.tharun.mymusic.feature.settings.SettingsViewModel;
import com.tharun.mymusic.feature.settings.SettingsViewModel_HiltModules;
import com.tharun.mymusic.feature.settings.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.tharun.mymusic.feature.settings.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.tharun.mymusic.service.AudioEffectController;
import com.tharun.mymusic.service.MyMusicPlaybackService;
import com.tharun.mymusic.service.MyMusicPlaybackService_MembersInjector;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import kotlinx.serialization.json.Json;
import okhttp3.OkHttpClient;

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
public final class DaggerMyMusicApp_HiltComponents_SingletonC {
  private DaggerMyMusicApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MyMusicApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MyMusicApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public MyMusicApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements MyMusicApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MyMusicApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MyMusicApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MyMusicApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MyMusicApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MyMusicApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MyMusicApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MyMusicApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MyMusicApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MyMusicApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MyMusicApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MyMusicApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MyMusicApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MyMusicApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends MyMusicApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MyMusicApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(8).put(EqualizerViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, EqualizerViewModel_HiltModules.KeyModule.provide()).put(HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HomeViewModel_HiltModules.KeyModule.provide()).put(LibraryViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, LibraryViewModel_HiltModules.KeyModule.provide()).put(LyricsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, LyricsViewModel_HiltModules.KeyModule.provide()).put(PlayerViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PlayerViewModel_HiltModules.KeyModule.provide()).put(PlaylistDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PlaylistDetailViewModel_HiltModules.KeyModule.provide()).put(SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SearchViewModel_HiltModules.KeyModule.provide()).put(SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectUserPreferencesRepository(instance, singletonCImpl.userPreferencesRepositoryImplProvider.get());
      MainActivity_MembersInjector.injectSongRepository(instance, singletonCImpl.songRepositoryImplProvider.get());
      MainActivity_MembersInjector.injectPlaylistRepository(instance, singletonCImpl.playlistRepositoryImplProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends MyMusicApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<EqualizerViewModel> equalizerViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<LibraryViewModel> libraryViewModelProvider;

    private Provider<LyricsViewModel> lyricsViewModelProvider;

    private Provider<PlayerViewModel> playerViewModelProvider;

    private Provider<PlaylistDetailViewModel> playlistDetailViewModelProvider;

    private Provider<SearchViewModel> searchViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private GetHomeDataUseCase getHomeDataUseCase() {
      return new GetHomeDataUseCase(singletonCImpl.recentlyPlayedRepositoryImplProvider.get(), singletonCImpl.musicDiscoveryRepositoryImplProvider.get(), singletonCImpl.favoriteRepositoryImplProvider.get());
    }

    private ScanDeviceAudioUseCase scanDeviceAudioUseCase() {
      return new ScanDeviceAudioUseCase(singletonCImpl.songRepositoryImplProvider.get());
    }

    private GetLyricsUseCase getLyricsUseCase() {
      return new GetLyricsUseCase(singletonCImpl.lyricsRepositoryImplProvider.get());
    }

    private SearchMusicUseCase searchMusicUseCase() {
      return new SearchMusicUseCase(singletonCImpl.musicDiscoveryRepositoryImplProvider.get(), singletonCImpl.songRepositoryImplProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.equalizerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.libraryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.lyricsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.playerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.playlistDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.searchViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(8).put(EqualizerViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) equalizerViewModelProvider)).put(HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) homeViewModelProvider)).put(LibraryViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) libraryViewModelProvider)).put(LyricsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) lyricsViewModelProvider)).put(PlayerViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) playerViewModelProvider)).put(PlaylistDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) playlistDetailViewModelProvider)).put(SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) searchViewModelProvider)).put(SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) settingsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.tharun.mymusic.feature.equalizer.EqualizerViewModel 
          return (T) new EqualizerViewModel(singletonCImpl.userPreferencesRepositoryImplProvider.get(), singletonCImpl.audioEffectControllerProvider.get());

          case 1: // com.tharun.mymusic.feature.home.HomeViewModel 
          return (T) new HomeViewModel(viewModelCImpl.getHomeDataUseCase(), singletonCImpl.playbackRepositoryImplProvider.get(), singletonCImpl.favoriteRepositoryImplProvider.get(), singletonCImpl.playlistRepositoryImplProvider.get());

          case 2: // com.tharun.mymusic.feature.library.LibraryViewModel 
          return (T) new LibraryViewModel(singletonCImpl.songRepositoryImplProvider.get(), singletonCImpl.playlistRepositoryImplProvider.get(), singletonCImpl.favoriteRepositoryImplProvider.get(), singletonCImpl.playbackRepositoryImplProvider.get(), viewModelCImpl.scanDeviceAudioUseCase());

          case 3: // com.tharun.mymusic.feature.lyrics.LyricsViewModel 
          return (T) new LyricsViewModel(viewModelCImpl.getLyricsUseCase(), singletonCImpl.songRepositoryImplProvider.get(), singletonCImpl.playbackRepositoryImplProvider.get(), viewModelCImpl.savedStateHandle);

          case 4: // com.tharun.mymusic.feature.player.PlayerViewModel 
          return (T) new PlayerViewModel(singletonCImpl.playbackRepositoryImplProvider.get(), singletonCImpl.favoriteRepositoryImplProvider.get());

          case 5: // com.tharun.mymusic.feature.playlists.PlaylistDetailViewModel 
          return (T) new PlaylistDetailViewModel(singletonCImpl.playlistRepositoryImplProvider.get(), singletonCImpl.playbackRepositoryImplProvider.get(), singletonCImpl.favoriteRepositoryImplProvider.get(), viewModelCImpl.savedStateHandle);

          case 6: // com.tharun.mymusic.feature.search.SearchViewModel 
          return (T) new SearchViewModel(viewModelCImpl.searchMusicUseCase(), singletonCImpl.searchHistoryRepositoryImplProvider.get(), singletonCImpl.playbackRepositoryImplProvider.get(), singletonCImpl.favoriteRepositoryImplProvider.get());

          case 7: // com.tharun.mymusic.feature.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.userPreferencesRepositoryImplProvider.get(), singletonCImpl.searchHistoryRepositoryImplProvider.get(), singletonCImpl.recentlyPlayedRepositoryImplProvider.get(), singletonCImpl.songRepositoryImplProvider.get(), singletonCImpl.playlistRepositoryImplProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends MyMusicApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends MyMusicApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectMyMusicPlaybackService(MyMusicPlaybackService myMusicPlaybackService) {
      injectMyMusicPlaybackService2(myMusicPlaybackService);
    }

    private MyMusicPlaybackService injectMyMusicPlaybackService2(MyMusicPlaybackService instance) {
      MyMusicPlaybackService_MembersInjector.injectAudioEffectController(instance, singletonCImpl.audioEffectControllerProvider.get());
      MyMusicPlaybackService_MembersInjector.injectPreferencesDataStore(instance, singletonCImpl.userPreferencesDataStoreProvider.get());
      MyMusicPlaybackService_MembersInjector.injectSongDao(instance, singletonCImpl.songDao());
      MyMusicPlaybackService_MembersInjector.injectFavoriteDao(instance, singletonCImpl.favoriteDao());
      MyMusicPlaybackService_MembersInjector.injectPlaylistDao(instance, singletonCImpl.playlistDao());
      MyMusicPlaybackService_MembersInjector.injectRecentlyPlayedDao(instance, singletonCImpl.recentlyPlayedDao());
      return instance;
    }
  }

  private static final class SingletonCImpl extends MyMusicApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<UserPreferencesDataStore> userPreferencesDataStoreProvider;

    private Provider<UserPreferencesRepositoryImpl> userPreferencesRepositoryImplProvider;

    private Provider<MyMusicDatabase> provideDatabaseProvider;

    private Provider<LocalDeviceDiscoveryProvider> localDeviceDiscoveryProvider;

    private Provider<DispatcherProvider> provideDispatcherProvider;

    private Provider<SongRepositoryImpl> songRepositoryImplProvider;

    private Provider<PlaylistRepositoryImpl> playlistRepositoryImplProvider;

    private Provider<AudioEffectController> audioEffectControllerProvider;

    private Provider<RecentlyPlayedRepositoryImpl> recentlyPlayedRepositoryImplProvider;

    private Provider<SampleDiscoveryProvider> sampleDiscoveryProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Json> provideJsonProvider;

    private Provider<YouTubeApiService> provideYouTubeApiServiceProvider;

    private Provider<YouTubeDiscoveryProvider> youTubeDiscoveryProvider;

    private Provider<MusicDiscoveryRepositoryImpl> musicDiscoveryRepositoryImplProvider;

    private Provider<FavoriteRepositoryImpl> favoriteRepositoryImplProvider;

    private Provider<PlaybackRepositoryImpl> playbackRepositoryImplProvider;

    private Provider<LyricsRepositoryImpl> lyricsRepositoryImplProvider;

    private Provider<SearchHistoryRepositoryImpl> searchHistoryRepositoryImplProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private SongDao songDao() {
      return DatabaseModule_ProvideSongDaoFactory.provideSongDao(provideDatabaseProvider.get());
    }

    private FavoriteDao favoriteDao() {
      return DatabaseModule_ProvideFavoriteDaoFactory.provideFavoriteDao(provideDatabaseProvider.get());
    }

    private PlaylistDao playlistDao() {
      return DatabaseModule_ProvidePlaylistDaoFactory.providePlaylistDao(provideDatabaseProvider.get());
    }

    private RecentlyPlayedDao recentlyPlayedDao() {
      return DatabaseModule_ProvideRecentlyPlayedDaoFactory.provideRecentlyPlayedDao(provideDatabaseProvider.get());
    }

    private LyricsDao lyricsDao() {
      return DatabaseModule_ProvideLyricsDaoFactory.provideLyricsDao(provideDatabaseProvider.get());
    }

    private SearchHistoryDao searchHistoryDao() {
      return DatabaseModule_ProvideSearchHistoryDaoFactory.provideSearchHistoryDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.userPreferencesDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<UserPreferencesDataStore>(singletonCImpl, 1));
      this.userPreferencesRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<UserPreferencesRepositoryImpl>(singletonCImpl, 0));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<MyMusicDatabase>(singletonCImpl, 3));
      this.localDeviceDiscoveryProvider = DoubleCheck.provider(new SwitchingProvider<LocalDeviceDiscoveryProvider>(singletonCImpl, 4));
      this.provideDispatcherProvider = DoubleCheck.provider(new SwitchingProvider<DispatcherProvider>(singletonCImpl, 5));
      this.songRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<SongRepositoryImpl>(singletonCImpl, 2));
      this.playlistRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<PlaylistRepositoryImpl>(singletonCImpl, 6));
      this.audioEffectControllerProvider = DoubleCheck.provider(new SwitchingProvider<AudioEffectController>(singletonCImpl, 7));
      this.recentlyPlayedRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<RecentlyPlayedRepositoryImpl>(singletonCImpl, 8));
      this.sampleDiscoveryProvider = DoubleCheck.provider(new SwitchingProvider<SampleDiscoveryProvider>(singletonCImpl, 10));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 13));
      this.provideJsonProvider = DoubleCheck.provider(new SwitchingProvider<Json>(singletonCImpl, 14));
      this.provideYouTubeApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<YouTubeApiService>(singletonCImpl, 12));
      this.youTubeDiscoveryProvider = DoubleCheck.provider(new SwitchingProvider<YouTubeDiscoveryProvider>(singletonCImpl, 11));
      this.musicDiscoveryRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<MusicDiscoveryRepositoryImpl>(singletonCImpl, 9));
      this.favoriteRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<FavoriteRepositoryImpl>(singletonCImpl, 15));
      this.playbackRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<PlaybackRepositoryImpl>(singletonCImpl, 16));
      this.lyricsRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<LyricsRepositoryImpl>(singletonCImpl, 17));
      this.searchHistoryRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<SearchHistoryRepositoryImpl>(singletonCImpl, 18));
    }

    @Override
    public void injectMyMusicApp(MyMusicApp myMusicApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.tharun.mymusic.data.repository.UserPreferencesRepositoryImpl 
          return (T) new UserPreferencesRepositoryImpl(singletonCImpl.userPreferencesDataStoreProvider.get());

          case 1: // com.tharun.mymusic.data.local.preferences.UserPreferencesDataStore 
          return (T) new UserPreferencesDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.tharun.mymusic.data.repository.SongRepositoryImpl 
          return (T) new SongRepositoryImpl(singletonCImpl.songDao(), singletonCImpl.favoriteDao(), singletonCImpl.localDeviceDiscoveryProvider.get(), singletonCImpl.provideDispatcherProvider.get());

          case 3: // com.tharun.mymusic.data.local.db.MyMusicDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.tharun.mymusic.data.remote.discovery.LocalDeviceDiscoveryProvider 
          return (T) new LocalDeviceDiscoveryProvider(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.tharun.mymusic.core.dispatchers.DispatcherProvider 
          return (T) DispatchersModule_ProvideDispatcherProviderFactory.provideDispatcherProvider();

          case 6: // com.tharun.mymusic.data.repository.PlaylistRepositoryImpl 
          return (T) new PlaylistRepositoryImpl(singletonCImpl.playlistDao(), singletonCImpl.songDao(), singletonCImpl.favoriteDao(), singletonCImpl.provideDispatcherProvider.get());

          case 7: // com.tharun.mymusic.service.AudioEffectController 
          return (T) new AudioEffectController();

          case 8: // com.tharun.mymusic.data.repository.RecentlyPlayedRepositoryImpl 
          return (T) new RecentlyPlayedRepositoryImpl(singletonCImpl.recentlyPlayedDao(), singletonCImpl.favoriteDao(), singletonCImpl.provideDispatcherProvider.get());

          case 9: // com.tharun.mymusic.data.repository.MusicDiscoveryRepositoryImpl 
          return (T) new MusicDiscoveryRepositoryImpl(singletonCImpl.sampleDiscoveryProvider.get(), singletonCImpl.youTubeDiscoveryProvider.get(), singletonCImpl.songRepositoryImplProvider.get(), singletonCImpl.playlistRepositoryImplProvider.get(), singletonCImpl.provideDispatcherProvider.get());

          case 10: // com.tharun.mymusic.data.remote.discovery.SampleDiscoveryProvider 
          return (T) new SampleDiscoveryProvider();

          case 11: // com.tharun.mymusic.data.remote.discovery.YouTubeDiscoveryProvider 
          return (T) new YouTubeDiscoveryProvider(singletonCImpl.provideYouTubeApiServiceProvider.get(), singletonCImpl.userPreferencesDataStoreProvider.get());

          case 12: // com.tharun.mymusic.data.remote.youtube.YouTubeApiService 
          return (T) NetworkModule_ProvideYouTubeApiServiceFactory.provideYouTubeApiService(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideJsonProvider.get());

          case 13: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 14: // kotlinx.serialization.json.Json 
          return (T) NetworkModule_ProvideJsonFactory.provideJson();

          case 15: // com.tharun.mymusic.data.repository.FavoriteRepositoryImpl 
          return (T) new FavoriteRepositoryImpl(singletonCImpl.favoriteDao(), singletonCImpl.provideDispatcherProvider.get());

          case 16: // com.tharun.mymusic.data.repository.PlaybackRepositoryImpl 
          return (T) new PlaybackRepositoryImpl(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideDispatcherProvider.get());

          case 17: // com.tharun.mymusic.data.repository.LyricsRepositoryImpl 
          return (T) new LyricsRepositoryImpl(singletonCImpl.lyricsDao(), singletonCImpl.provideDispatcherProvider.get());

          case 18: // com.tharun.mymusic.data.repository.SearchHistoryRepositoryImpl 
          return (T) new SearchHistoryRepositoryImpl(singletonCImpl.searchHistoryDao(), singletonCImpl.provideDispatcherProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
