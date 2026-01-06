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

        // Query che unisce User e UserStats con LEFT JOIN per non escludere utenti senza statistiche
        // Escludiamo gli utenti con ruolo 'admin' dalla classifica
        String query = "SELECT u.id, u.name, COALESCE(s.totalPoints, 0) as totalPoints " +
                "FROM " + DBHelper.T_USER + " u " +
                "LEFT JOIN " + DBHelper.T_USER_STATS + " s ON u.id = s.userId " +
                "WHERE u.role != 'admin' " +
                "ORDER BY totalPoints DESC";

        Cursor cursor = db.rawQuery(query, null);
        int pos = 1;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new LeaderboardEntry(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("totalPoints")),
                        pos++
                ));
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return list;
    }
}
