package com.tharun.mymusic.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tharun.mymusic.data.local.entity.LyricsEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LyricsDao_Impl implements LyricsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LyricsEntity> __insertionAdapterOfLyricsEntity;

  public LyricsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLyricsEntity = new EntityInsertionAdapter<LyricsEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `lyrics` (`songId`,`plainText`,`lrcContent`,`cachedAt`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LyricsEntity entity) {
        statement.bindString(1, entity.getSongId());
        if (entity.getPlainText() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getPlainText());
        }
        if (entity.getLrcContent() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getLrcContent());
        }
        statement.bindLong(4, entity.getCachedAt());
      }
    };
  }

  @Override
  public Object insertLyrics(final LyricsEntity lyrics,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLyricsEntity.insert(lyrics);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<LyricsEntity> getLyrics(final String songId) {
    final String _sql = "SELECT * FROM lyrics WHERE songId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, songId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"lyrics"}, new Callable<LyricsEntity>() {
      @Override
      @Nullable
      public LyricsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSongId = CursorUtil.getColumnIndexOrThrow(_cursor, "songId");
          final int _cursorIndexOfPlainText = CursorUtil.getColumnIndexOrThrow(_cursor, "plainText");
          final int _cursorIndexOfLrcContent = CursorUtil.getColumnIndexOrThrow(_cursor, "lrcContent");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final LyricsEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpSongId;
            _tmpSongId = _cursor.getString(_cursorIndexOfSongId);
            final String _tmpPlainText;
            if (_cursor.isNull(_cursorIndexOfPlainText)) {
              _tmpPlainText = null;
            } else {
              _tmpPlainText = _cursor.getString(_cursorIndexOfPlainText);
            }
            final String _tmpLrcContent;
            if (_cursor.isNull(_cursorIndexOfLrcContent)) {
              _tmpLrcContent = null;
            } else {
              _tmpLrcContent = _cursor.getString(_cursorIndexOfLrcContent);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new LyricsEntity(_tmpSongId,_tmpPlainText,_tmpLrcContent,_tmpCachedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
