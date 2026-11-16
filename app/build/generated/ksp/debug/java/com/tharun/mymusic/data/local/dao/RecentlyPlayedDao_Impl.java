package com.tharun.mymusic.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tharun.mymusic.data.local.entity.RecentlyPlayedEntity;
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
public final class RecentlyPlayedDao_Impl implements RecentlyPlayedDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RecentlyPlayedEntity> __insertionAdapterOfRecentlyPlayedEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public RecentlyPlayedDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRecentlyPlayedEntity = new EntityInsertionAdapter<RecentlyPlayedEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `recently_played` (`songId`,`playedAt`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecentlyPlayedEntity entity) {
        statement.bindString(1, entity.getSongId());
        statement.bindLong(2, entity.getPlayedAt());
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM recently_played";
        return _query;
      }
    };
  }

  @Override
  public Object insertRecentlyPlayed(final RecentlyPlayedEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRecentlyPlayedEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SongEntity>> getRecentlyPlayed(final int limit) {
    final String _sql = "\n"
            + "        SELECT s.* FROM songs s\n"
            + "        INNER JOIN recently_played rp ON s.id = rp.songId\n"
            + "        ORDER BY rp.playedAt DESC\n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"songs",
        "recently_played"}, new Callable<List<SongEntity>>() {
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
