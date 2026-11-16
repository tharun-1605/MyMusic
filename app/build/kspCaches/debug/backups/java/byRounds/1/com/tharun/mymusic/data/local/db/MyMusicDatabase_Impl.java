package com.tharun.mymusic.data.local.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.tharun.mymusic.data.local.dao.FavoriteDao;
import com.tharun.mymusic.data.local.dao.FavoriteDao_Impl;
import com.tharun.mymusic.data.local.dao.LyricsDao;
import com.tharun.mymusic.data.local.dao.LyricsDao_Impl;
import com.tharun.mymusic.data.local.dao.PlaylistDao;
import com.tharun.mymusic.data.local.dao.PlaylistDao_Impl;
import com.tharun.mymusic.data.local.dao.RecentlyPlayedDao;
import com.tharun.mymusic.data.local.dao.RecentlyPlayedDao_Impl;
import com.tharun.mymusic.data.local.dao.SearchHistoryDao;
import com.tharun.mymusic.data.local.dao.SearchHistoryDao_Impl;
import com.tharun.mymusic.data.local.dao.SongDao;
import com.tharun.mymusic.data.local.dao.SongDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MyMusicDatabase_Impl extends MyMusicDatabase {
  private volatile SongDao _songDao;

  private volatile PlaylistDao _playlistDao;

  private volatile FavoriteDao _favoriteDao;

  private volatile RecentlyPlayedDao _recentlyPlayedDao;

  private volatile SearchHistoryDao _searchHistoryDao;

  private volatile LyricsDao _lyricsDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `songs` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `artist` TEXT NOT NULL, `album` TEXT NOT NULL, `artworkUri` TEXT, `mediaUri` TEXT, `durationMs` INTEGER NOT NULL, `isPlayable` INTEGER NOT NULL, `provider` TEXT NOT NULL, `youtubeVideoId` TEXT, `dateAdded` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `playlists` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `artworkUri` TEXT, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `playlist_songs` (`playlistId` INTEGER NOT NULL, `songId` TEXT NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`playlistId`, `songId`), FOREIGN KEY(`playlistId`) REFERENCES `playlists`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`songId`) REFERENCES `songs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_playlist_songs_playlistId` ON `playlist_songs` (`playlistId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_playlist_songs_songId` ON `playlist_songs` (`songId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `favorites` (`songId` TEXT NOT NULL, `addedAt` INTEGER NOT NULL, PRIMARY KEY(`songId`), FOREIGN KEY(`songId`) REFERENCES `songs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `recently_played` (`songId` TEXT NOT NULL, `playedAt` INTEGER NOT NULL, PRIMARY KEY(`songId`), FOREIGN KEY(`songId`) REFERENCES `songs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `search_history` (`query` TEXT NOT NULL, `searchedAt` INTEGER NOT NULL, PRIMARY KEY(`query`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `lyrics` (`songId` TEXT NOT NULL, `plainText` TEXT, `lrcContent` TEXT, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`songId`), FOREIGN KEY(`songId`) REFERENCES `songs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6001facbc1b0a6931e7e09d2fa569144')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `songs`");
        db.execSQL("DROP TABLE IF EXISTS `playlists`");
        db.execSQL("DROP TABLE IF EXISTS `playlist_songs`");
        db.execSQL("DROP TABLE IF EXISTS `favorites`");
        db.execSQL("DROP TABLE IF EXISTS `recently_played`");
        db.execSQL("DROP TABLE IF EXISTS `search_history`");
        db.execSQL("DROP TABLE IF EXISTS `lyrics`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsSongs = new HashMap<String, TableInfo.Column>(11);
        _columnsSongs.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSongs.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSongs.put("artist", new TableInfo.Column("artist", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSongs.put("album", new TableInfo.Column("album", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSongs.put("artworkUri", new TableInfo.Column("artworkUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSongs.put("mediaUri", new TableInfo.Column("mediaUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSongs.put("durationMs", new TableInfo.Column("durationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSongs.put("isPlayable", new TableInfo.Column("isPlayable", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSongs.put("provider", new TableInfo.Column("provider", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSongs.put("youtubeVideoId", new TableInfo.Column("youtubeVideoId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSongs.put("dateAdded", new TableInfo.Column("dateAdded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSongs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSongs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSongs = new TableInfo("songs", _columnsSongs, _foreignKeysSongs, _indicesSongs);
        final TableInfo _existingSongs = TableInfo.read(db, "songs");
        if (!_infoSongs.equals(_existingSongs)) {
          return new RoomOpenHelper.ValidationResult(false, "songs(com.tharun.mymusic.data.local.entity.SongEntity).\n"
                  + " Expected:\n" + _infoSongs + "\n"
                  + " Found:\n" + _existingSongs);
        }
        final HashMap<String, TableInfo.Column> _columnsPlaylists = new HashMap<String, TableInfo.Column>(5);
        _columnsPlaylists.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("artworkUri", new TableInfo.Column("artworkUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlaylists = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPlaylists = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPlaylists = new TableInfo("playlists", _columnsPlaylists, _foreignKeysPlaylists, _indicesPlaylists);
        final TableInfo _existingPlaylists = TableInfo.read(db, "playlists");
        if (!_infoPlaylists.equals(_existingPlaylists)) {
          return new RoomOpenHelper.ValidationResult(false, "playlists(com.tharun.mymusic.data.local.entity.PlaylistEntity).\n"
                  + " Expected:\n" + _infoPlaylists + "\n"
                  + " Found:\n" + _existingPlaylists);
        }
        final HashMap<String, TableInfo.Column> _columnsPlaylistSongs = new HashMap<String, TableInfo.Column>(3);
        _columnsPlaylistSongs.put("playlistId", new TableInfo.Column("playlistId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylistSongs.put("songId", new TableInfo.Column("songId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylistSongs.put("sortOrder", new TableInfo.Column("sortOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlaylistSongs = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysPlaylistSongs.add(new TableInfo.ForeignKey("playlists", "CASCADE", "NO ACTION", Arrays.asList("playlistId"), Arrays.asList("id")));
        _foreignKeysPlaylistSongs.add(new TableInfo.ForeignKey("songs", "CASCADE", "NO ACTION", Arrays.asList("songId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPlaylistSongs = new HashSet<TableInfo.Index>(2);
        _indicesPlaylistSongs.add(new TableInfo.Index("index_playlist_songs_playlistId", false, Arrays.asList("playlistId"), Arrays.asList("ASC")));
        _indicesPlaylistSongs.add(new TableInfo.Index("index_playlist_songs_songId", false, Arrays.asList("songId"), Arrays.asList("ASC")));
        final TableInfo _infoPlaylistSongs = new TableInfo("playlist_songs", _columnsPlaylistSongs, _foreignKeysPlaylistSongs, _indicesPlaylistSongs);
        final TableInfo _existingPlaylistSongs = TableInfo.read(db, "playlist_songs");
        if (!_infoPlaylistSongs.equals(_existingPlaylistSongs)) {
          return new RoomOpenHelper.ValidationResult(false, "playlist_songs(com.tharun.mymusic.data.local.entity.PlaylistSongCrossRef).\n"
                  + " Expected:\n" + _infoPlaylistSongs + "\n"
                  + " Found:\n" + _existingPlaylistSongs);
        }
        final HashMap<String, TableInfo.Column> _columnsFavorites = new HashMap<String, TableInfo.Column>(2);
        _columnsFavorites.put("songId", new TableInfo.Column("songId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("addedAt", new TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFavorites = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysFavorites.add(new TableInfo.ForeignKey("songs", "CASCADE", "NO ACTION", Arrays.asList("songId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesFavorites = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFavorites = new TableInfo("favorites", _columnsFavorites, _foreignKeysFavorites, _indicesFavorites);
        final TableInfo _existingFavorites = TableInfo.read(db, "favorites");
        if (!_infoFavorites.equals(_existingFavorites)) {
          return new RoomOpenHelper.ValidationResult(false, "favorites(com.tharun.mymusic.data.local.entity.FavoriteEntity).\n"
                  + " Expected:\n" + _infoFavorites + "\n"
                  + " Found:\n" + _existingFavorites);
        }
        final HashMap<String, TableInfo.Column> _columnsRecentlyPlayed = new HashMap<String, TableInfo.Column>(2);
        _columnsRecentlyPlayed.put("songId", new TableInfo.Column("songId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentlyPlayed.put("playedAt", new TableInfo.Column("playedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecentlyPlayed = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRecentlyPlayed.add(new TableInfo.ForeignKey("songs", "CASCADE", "NO ACTION", Arrays.asList("songId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRecentlyPlayed = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecentlyPlayed = new TableInfo("recently_played", _columnsRecentlyPlayed, _foreignKeysRecentlyPlayed, _indicesRecentlyPlayed);
        final TableInfo _existingRecentlyPlayed = TableInfo.read(db, "recently_played");
        if (!_infoRecentlyPlayed.equals(_existingRecentlyPlayed)) {
          return new RoomOpenHelper.ValidationResult(false, "recently_played(com.tharun.mymusic.data.local.entity.RecentlyPlayedEntity).\n"
                  + " Expected:\n" + _infoRecentlyPlayed + "\n"
                  + " Found:\n" + _existingRecentlyPlayed);
        }
        final HashMap<String, TableInfo.Column> _columnsSearchHistory = new HashMap<String, TableInfo.Column>(2);
        _columnsSearchHistory.put("query", new TableInfo.Column("query", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("searchedAt", new TableInfo.Column("searchedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSearchHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSearchHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSearchHistory = new TableInfo("search_history", _columnsSearchHistory, _foreignKeysSearchHistory, _indicesSearchHistory);
        final TableInfo _existingSearchHistory = TableInfo.read(db, "search_history");
        if (!_infoSearchHistory.equals(_existingSearchHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "search_history(com.tharun.mymusic.data.local.entity.SearchHistoryEntity).\n"
                  + " Expected:\n" + _infoSearchHistory + "\n"
                  + " Found:\n" + _existingSearchHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsLyrics = new HashMap<String, TableInfo.Column>(4);
        _columnsLyrics.put("songId", new TableInfo.Column("songId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLyrics.put("plainText", new TableInfo.Column("plainText", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLyrics.put("lrcContent", new TableInfo.Column("lrcContent", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLyrics.put("cachedAt", new TableInfo.Column("cachedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLyrics = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysLyrics.add(new TableInfo.ForeignKey("songs", "CASCADE", "NO ACTION", Arrays.asList("songId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesLyrics = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLyrics = new TableInfo("lyrics", _columnsLyrics, _foreignKeysLyrics, _indicesLyrics);
        final TableInfo _existingLyrics = TableInfo.read(db, "lyrics");
        if (!_infoLyrics.equals(_existingLyrics)) {
          return new RoomOpenHelper.ValidationResult(false, "lyrics(com.tharun.mymusic.data.local.entity.LyricsEntity).\n"
                  + " Expected:\n" + _infoLyrics + "\n"
                  + " Found:\n" + _existingLyrics);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "6001facbc1b0a6931e7e09d2fa569144", "7d0fdb4d0b9f972a6cd891eb39f60def");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "songs","playlists","playlist_songs","favorites","recently_played","search_history","lyrics");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `songs`");
      _db.execSQL("DELETE FROM `playlists`");
      _db.execSQL("DELETE FROM `playlist_songs`");
      _db.execSQL("DELETE FROM `favorites`");
      _db.execSQL("DELETE FROM `recently_played`");
      _db.execSQL("DELETE FROM `search_history`");
      _db.execSQL("DELETE FROM `lyrics`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(SongDao.class, SongDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PlaylistDao.class, PlaylistDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FavoriteDao.class, FavoriteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RecentlyPlayedDao.class, RecentlyPlayedDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SearchHistoryDao.class, SearchHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LyricsDao.class, LyricsDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public SongDao songDao() {
    if (_songDao != null) {
      return _songDao;
    } else {
      synchronized(this) {
        if(_songDao == null) {
          _songDao = new SongDao_Impl(this);
        }
        return _songDao;
      }
    }
  }

  @Override
  public PlaylistDao playlistDao() {
    if (_playlistDao != null) {
      return _playlistDao;
    } else {
      synchronized(this) {
        if(_playlistDao == null) {
          _playlistDao = new PlaylistDao_Impl(this);
        }
        return _playlistDao;
      }
    }
  }

  @Override
  public FavoriteDao favoriteDao() {
    if (_favoriteDao != null) {
      return _favoriteDao;
    } else {
      synchronized(this) {
        if(_favoriteDao == null) {
          _favoriteDao = new FavoriteDao_Impl(this);
        }
        return _favoriteDao;
      }
    }
  }

  @Override
  public RecentlyPlayedDao recentlyPlayedDao() {
    if (_recentlyPlayedDao != null) {
      return _recentlyPlayedDao;
    } else {
      synchronized(this) {
        if(_recentlyPlayedDao == null) {
          _recentlyPlayedDao = new RecentlyPlayedDao_Impl(this);
        }
        return _recentlyPlayedDao;
      }
    }
  }

  @Override
  public SearchHistoryDao searchHistoryDao() {
    if (_searchHistoryDao != null) {
      return _searchHistoryDao;
    } else {
      synchronized(this) {
        if(_searchHistoryDao == null) {
          _searchHistoryDao = new SearchHistoryDao_Impl(this);
        }
        return _searchHistoryDao;
      }
    }
  }

  @Override
  public LyricsDao lyricsDao() {
    if (_lyricsDao != null) {
      return _lyricsDao;
    } else {
      synchronized(this) {
        if(_lyricsDao == null) {
          _lyricsDao = new LyricsDao_Impl(this);
        }
        return _lyricsDao;
      }
    }
  }
}
