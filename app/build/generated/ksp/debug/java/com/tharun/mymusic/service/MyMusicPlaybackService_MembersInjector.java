package com.tharun.mymusic.service;

import com.tharun.mymusic.data.local.dao.FavoriteDao;
import com.tharun.mymusic.data.local.dao.PlaylistDao;
import com.tharun.mymusic.data.local.dao.RecentlyPlayedDao;
import com.tharun.mymusic.data.local.dao.SongDao;
import com.tharun.mymusic.data.local.preferences.UserPreferencesDataStore;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class MyMusicPlaybackService_MembersInjector implements MembersInjector<MyMusicPlaybackService> {
  private final Provider<AudioEffectController> audioEffectControllerProvider;

  private final Provider<UserPreferencesDataStore> preferencesDataStoreProvider;

  private final Provider<SongDao> songDaoProvider;

  private final Provider<FavoriteDao> favoriteDaoProvider;

  private final Provider<PlaylistDao> playlistDaoProvider;

  private final Provider<RecentlyPlayedDao> recentlyPlayedDaoProvider;

  public MyMusicPlaybackService_MembersInjector(
      Provider<AudioEffectController> audioEffectControllerProvider,
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider,
      Provider<SongDao> songDaoProvider, Provider<FavoriteDao> favoriteDaoProvider,
      Provider<PlaylistDao> playlistDaoProvider,
      Provider<RecentlyPlayedDao> recentlyPlayedDaoProvider) {
    this.audioEffectControllerProvider = audioEffectControllerProvider;
    this.preferencesDataStoreProvider = preferencesDataStoreProvider;
    this.songDaoProvider = songDaoProvider;
    this.favoriteDaoProvider = favoriteDaoProvider;
    this.playlistDaoProvider = playlistDaoProvider;
    this.recentlyPlayedDaoProvider = recentlyPlayedDaoProvider;
  }

  public static MembersInjector<MyMusicPlaybackService> create(
      Provider<AudioEffectController> audioEffectControllerProvider,
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider,
      Provider<SongDao> songDaoProvider, Provider<FavoriteDao> favoriteDaoProvider,
      Provider<PlaylistDao> playlistDaoProvider,
      Provider<RecentlyPlayedDao> recentlyPlayedDaoProvider) {
    return new MyMusicPlaybackService_MembersInjector(audioEffectControllerProvider, preferencesDataStoreProvider, songDaoProvider, favoriteDaoProvider, playlistDaoProvider, recentlyPlayedDaoProvider);
  }

  public static MembersInjector<MyMusicPlaybackService> create(
      javax.inject.Provider<AudioEffectController> audioEffectControllerProvider,
      javax.inject.Provider<UserPreferencesDataStore> preferencesDataStoreProvider,
      javax.inject.Provider<SongDao> songDaoProvider,
      javax.inject.Provider<FavoriteDao> favoriteDaoProvider,
      javax.inject.Provider<PlaylistDao> playlistDaoProvider,
      javax.inject.Provider<RecentlyPlayedDao> recentlyPlayedDaoProvider) {
    return new MyMusicPlaybackService_MembersInjector(Providers.asDaggerProvider(audioEffectControllerProvider), Providers.asDaggerProvider(preferencesDataStoreProvider), Providers.asDaggerProvider(songDaoProvider), Providers.asDaggerProvider(favoriteDaoProvider), Providers.asDaggerProvider(playlistDaoProvider), Providers.asDaggerProvider(recentlyPlayedDaoProvider));
  }

  @Override
  public void injectMembers(MyMusicPlaybackService instance) {
    injectAudioEffectController(instance, audioEffectControllerProvider.get());
    injectPreferencesDataStore(instance, preferencesDataStoreProvider.get());
    injectSongDao(instance, songDaoProvider.get());
    injectFavoriteDao(instance, favoriteDaoProvider.get());
    injectPlaylistDao(instance, playlistDaoProvider.get());
    injectRecentlyPlayedDao(instance, recentlyPlayedDaoProvider.get());
  }

  @InjectedFieldSignature("com.tharun.mymusic.service.MyMusicPlaybackService.audioEffectController")
  public static void injectAudioEffectController(MyMusicPlaybackService instance,
      AudioEffectController audioEffectController) {
    instance.audioEffectController = audioEffectController;
  }

  @InjectedFieldSignature("com.tharun.mymusic.service.MyMusicPlaybackService.preferencesDataStore")
  public static void injectPreferencesDataStore(MyMusicPlaybackService instance,
      UserPreferencesDataStore preferencesDataStore) {
    instance.preferencesDataStore = preferencesDataStore;
  }

  @InjectedFieldSignature("com.tharun.mymusic.service.MyMusicPlaybackService.songDao")
  public static void injectSongDao(MyMusicPlaybackService instance, SongDao songDao) {
    instance.songDao = songDao;
  }

  @InjectedFieldSignature("com.tharun.mymusic.service.MyMusicPlaybackService.favoriteDao")
  public static void injectFavoriteDao(MyMusicPlaybackService instance, FavoriteDao favoriteDao) {
    instance.favoriteDao = favoriteDao;
  }

  @InjectedFieldSignature("com.tharun.mymusic.service.MyMusicPlaybackService.playlistDao")
  public static void injectPlaylistDao(MyMusicPlaybackService instance, PlaylistDao playlistDao) {
    instance.playlistDao = playlistDao;
  }

  @InjectedFieldSignature("com.tharun.mymusic.service.MyMusicPlaybackService.recentlyPlayedDao")
  public static void injectRecentlyPlayedDao(MyMusicPlaybackService instance,
      RecentlyPlayedDao recentlyPlayedDao) {
    instance.recentlyPlayedDao = recentlyPlayedDao;
  }
}
