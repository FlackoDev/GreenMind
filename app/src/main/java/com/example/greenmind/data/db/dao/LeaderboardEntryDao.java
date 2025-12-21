package com.example.greenmind.data.db.dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.resource.model.LeaderboardEntry;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardEntryDao {

    private final DBHelper dbHelper;

    public LeaderboardEntryDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    @SuppressLint("Range")
    public List<LeaderboardEntry> getGlobalLeaderboard() {
        List<LeaderboardEntry> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query che unisce User e UserStats per avere nomi e punti totali
        String query = "SELECT u.id, u.name, s.totalPoints " +
                "FROM " + DBHelper.T_USER + " u " +
                "JOIN " + DBHelper.T_USER_STATS + " s ON u.id = s.userId " +
                "ORDER BY s.totalPoints DESC";

        Cursor cursor = db.rawQuery(query, null);
        int pos = 1;
        if (cursor.moveToFirst()) {
            do {
                list.add(new LeaderboardEntry(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("totalPoints")),
                        pos++
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
