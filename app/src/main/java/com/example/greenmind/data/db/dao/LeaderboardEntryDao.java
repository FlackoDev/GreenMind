package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.model.LeaderboardEntry;

public class LeaderboardEntryDao {

    private final DBHelper dbHelper;

    public LeaderboardEntryDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long upsert(LeaderboardEntry e) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("userId", e.getUserId());
        cv.put("points", e.getPoints());
        cv.put("position", e.getPosition());

        return db.insertWithOnConflict(
                DBHelper.T_LEADERBOARD,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }
}
