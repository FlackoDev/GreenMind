package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.model.UserStats;

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
}
