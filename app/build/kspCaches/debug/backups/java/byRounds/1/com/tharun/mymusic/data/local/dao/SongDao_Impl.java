package com.tharun.mymusic.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tharun.mymusic.data.local.entity.SongEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SongDao_Impl implements SongDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SongEntity> __insertionAdapterOfSongEntity;

  public SongDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSongEntity = new EntityInsertionAdapter<SongEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `songs` (`id`,`title`,`artist`,`album`,`artworkUri`,`mediaUri`,`durationMs`,`isPlayable`,`provider`,`youtubeVideoId`,`dateAdded`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SongEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getArtist());
        statement.bindString(4, entity.getAlbum());
        if (entity.getArtworkUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getArtworkUri());
        }
        if (entity.getMediaUri() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getMediaUri());
        }
        statement.bindLong(7, entity.getDurationMs());
        final int _tmp = entity.isPlayable() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindString(9, entity.getProvider());
        if (entity.getYoutubeVideoId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getYoutubeVideoId());
        }
        statement.bindLong(11, entity.getDateAdded());
      }
    };
  }

  @Override
  public Object insertSong(final SongEntity song, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSongEntity.insert(song);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSongs(final List<SongEntity> songs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSongEntity.insert(songs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SongEntity>> getAllSongs() {
    final String _sql = "SELECT * FROM songs ORDER BY title ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"songs"}, new Callable<List<SongEntity>>() {
      @Override
      @NonNull
      public List<SongEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfArtworkUri = CursorUtil.getColumnIndexOrThrow(_cursor, "artworkUri");
          final int _cursorIndexOfMediaUri = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaUri");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfIsPlayable = CursorUtil.getColumnIndexOrThrow(_cursor, "isPlayable");
          final int _cursorIndexOfProvider = CursorUtil.getColumnIndexOrThrow(_cursor, "provider");
          final int _cursorIndexOfYoutubeVideoId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtubeVideoId");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final List<SongEntity> _result = new ArrayList<SongEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SongEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            final String _tmpAlbum;
            _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            final String _tmpArtworkUri;
            if (_cursor.isNull(_cursorIndexOfArtworkUri)) {
              _tmpArtworkUri = null;
            } else {
              _tmpArtworkUri = _cursor.getString(_cursorIndexOfArtworkUri);
            }
            final String _tmpMediaUri;
            if (_cursor.isNull(_cursorIndexOfMediaUri)) {
              _tmpMediaUri = null;
            } else {
              _tmpMediaUri = _cursor.getString(_cursorIndexOfMediaUri);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final boolean _tmpIsPlayable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPlayable);
            _tmpIsPlayable = _tmp != 0;
            final String _tmpProvider;
            _tmpProvider = _cursor.getString(_cursorIndexOfProvider);
            final String _tmpYoutubeVideoId;
            if (_cursor.isNull(_cursorIndexOfYoutubeVideoId)) {
              _tmpYoutubeVideoId = null;
            } else {
              _tmpYoutubeVideoId = _cursor.getString(_cursorIndexOfYoutubeVideoId);
            }
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            _item = new SongEntity(_tmpId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpArtworkUri,_tmpMediaUri,_tmpDurationMs,_tmpIsPlayable,_tmpProvider,_tmpYoutubeVideoId,_tmpDateAdded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<SongEntity> getSongById(final String id) {
    final String _sql = "SELECT * FROM songs WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"songs"}, new Callable<SongEntity>() {
      @Override
      @Nullable
      public SongEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfArtworkUri = CursorUtil.getColumnIndexOrThrow(_cursor, "artworkUri");
          final int _cursorIndexOfMediaUri = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaUri");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfIsPlayable = CursorUtil.getColumnIndexOrThrow(_cursor, "isPlayable");
          final int _cursorIndexOfProvider = CursorUtil.getColumnIndexOrThrow(_cursor, "provider");
          final int _cursorIndexOfYoutubeVideoId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtubeVideoId");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final SongEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            final String _tmpAlbum;
            _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            final String _tmpArtworkUri;
            if (_cursor.isNull(_cursorIndexOfArtworkUri)) {
              _tmpArtworkUri = null;
            } else {
              _tmpArtworkUri = _cursor.getString(_cursorIndexOfArtworkUri);
            }
            final String _tmpMediaUri;
            if (_cursor.isNull(_cursorIndexOfMediaUri)) {
              _tmpMediaUri = null;
            } else {
              _tmpMediaUri = _cursor.getString(_cursorIndexOfMediaUri);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final boolean _tmpIsPlayable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPlayable);
            _tmpIsPlayable = _tmp != 0;
            final String _tmpProvider;
            _tmpProvider = _cursor.getString(_cursorIndexOfProvider);
            final String _tmpYoutubeVideoId;
            if (_cursor.isNull(_cursorIndexOfYoutubeVideoId)) {
              _tmpYoutubeVideoId = null;
            } else {
              _tmpYoutubeVideoId = _cursor.getString(_cursorIndexOfYoutubeVideoId);
            }
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            _result = new SongEntity(_tmpId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpArtworkUri,_tmpMediaUri,_tmpDurationMs,_tmpIsPlayable,_tmpProvider,_tmpYoutubeVideoId,_tmpDateAdded);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getSongByIdSync(final String id,
      final Continuation<? super SongEntity> $completion) {
    final String _sql = "SELECT * FROM songs WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SongEntity>() {
      @Override
      @Nullable
      public SongEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfArtworkUri = CursorUtil.getColumnIndexOrThrow(_cursor, "artworkUri");
          final int _cursorIndexOfMediaUri = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaUri");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfIsPlayable = CursorUtil.getColumnIndexOrThrow(_cursor, "isPlayable");
          final int _cursorIndexOfProvider = CursorUtil.getColumnIndexOrThrow(_cursor, "provider");
          final int _cursorIndexOfYoutubeVideoId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtubeVideoId");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final SongEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            final String _tmpAlbum;
            _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            final String _tmpArtworkUri;
            if (_cursor.isNull(_cursorIndexOfArtworkUri)) {
              _tmpArtworkUri = null;
            } else {
              _tmpArtworkUri = _cursor.getString(_cursorIndexOfArtworkUri);
            }
            final String _tmpMediaUri;
            if (_cursor.isNull(_cursorIndexOfMediaUri)) {
              _tmpMediaUri = null;
            } else {
              _tmpMediaUri = _cursor.getString(_cursorIndexOfMediaUri);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final boolean _tmpIsPlayable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPlayable);
            _tmpIsPlayable = _tmp != 0;
            final String _tmpProvider;
            _tmpProvider = _cursor.getString(_cursorIndexOfProvider);
            final String _tmpYoutubeVideoId;
            if (_cursor.isNull(_cursorIndexOfYoutubeVideoId)) {
              _tmpYoutubeVideoId = null;
            } else {
              _tmpYoutubeVideoId = _cursor.getString(_cursorIndexOfYoutubeVideoId);
            }
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            _result = new SongEntity(_tmpId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpArtworkUri,_tmpMediaUri,_tmpDurationMs,_tmpIsPlayable,_tmpProvider,_tmpYoutubeVideoId,_tmpDateAdded);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchSongs(final String query,
      final Continuation<? super List<SongEntity>> $completion) {
    final String _sql = "SELECT * FROM songs WHERE title LIKE '%' || ? || '%' OR artist LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SongEntity>>() {
      @Override
      @NonNull
      public List<SongEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfArtist = CursorUtil.getColumnIndexOrThrow(_cursor, "artist");
          final int _cursorIndexOfAlbum = CursorUtil.getColumnIndexOrThrow(_cursor, "album");
          final int _cursorIndexOfArtworkUri = CursorUtil.getColumnIndexOrThrow(_cursor, "artworkUri");
          final int _cursorIndexOfMediaUri = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaUri");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfIsPlayable = CursorUtil.getColumnIndexOrThrow(_cursor, "isPlayable");
          final int _cursorIndexOfProvider = CursorUtil.getColumnIndexOrThrow(_cursor, "provider");
          final int _cursorIndexOfYoutubeVideoId = CursorUtil.getColumnIndexOrThrow(_cursor, "youtubeVideoId");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final List<SongEntity> _result = new ArrayList<SongEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SongEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpArtist;
            _tmpArtist = _cursor.getString(_cursorIndexOfArtist);
            final String _tmpAlbum;
            _tmpAlbum = _cursor.getString(_cursorIndexOfAlbum);
            final String _tmpArtworkUri;
            if (_cursor.isNull(_cursorIndexOfArtworkUri)) {
              _tmpArtworkUri = null;
            } else {
              _tmpArtworkUri = _cursor.getString(_cursorIndexOfArtworkUri);
            }
            final String _tmpMediaUri;
            if (_cursor.isNull(_cursorIndexOfMediaUri)) {
              _tmpMediaUri = null;
            } else {
              _tmpMediaUri = _cursor.getString(_cursorIndexOfMediaUri);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final boolean _tmpIsPlayable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPlayable);
            _tmpIsPlayable = _tmp != 0;
            final String _tmpProvider;
            _tmpProvider = _cursor.getString(_cursorIndexOfProvider);
            final String _tmpYoutubeVideoId;
            if (_cursor.isNull(_cursorIndexOfYoutubeVideoId)) {
              _tmpYoutubeVideoId = null;
            } else {
              _tmpYoutubeVideoId = _cursor.getString(_cursorIndexOfYoutubeVideoId);
            }
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            _item = new SongEntity(_tmpId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpArtworkUri,_tmpMediaUri,_tmpDurationMs,_tmpIsPlayable,_tmpProvider,_tmpYoutubeVideoId,_tmpDateAdded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
