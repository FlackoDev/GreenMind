package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.resource.model.UserStats;

public class UserStatsDao {

    private final DBHelper dbHelper;

    public UserStatsDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long upsert(UserStats s) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("userId", s.getUserId());
        cv.put("totalQuizzes", s.getTotalQuizzes());
        cv.put("totalPoints", s.getTotalPoints());
        cv.put("weeklyChangePerc", s.getWeeklyChangePerc());

        return db.insertWithOnConflict(
                DBHelper.T_USER_STATS,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public UserStats getStatsByUserId(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.T_USER_STATS, null, "userId=?", new String[]{String.valueOf(userId)}, null, null, null);
        
        if (c != null && c.moveToFirst()) {
            UserStats s = new UserStats(
                    c.getInt(c.getColumnIndexOrThrow("userId")),
                    c.getInt(c.getColumnIndexOrThrow("totalQuizzes")),
                    c.getInt(c.getColumnIndexOrThrow("totalPoints")),
                    c.getFloat(c.getColumnIndexOrThrow("weeklyChangePerc"))
            );
            c.close();
            return s;
        }
        if (c != null) c.close();
        return new UserStats(userId, 0, 0, 0);
    }
}
