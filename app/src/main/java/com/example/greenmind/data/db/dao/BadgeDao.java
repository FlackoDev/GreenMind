package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.model.Badge;

public class BadgeDao {

    private final DBHelper dbHelper;

    public BadgeDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long upsert(Badge b) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", b.getId());
        cv.put("name", b.getName());
        cv.put("description", b.getDescription());
        cv.put("requiredPoints", b.getRequiredPoints());

        return db.insertWithOnConflict(
                DBHelper.T_BADGE,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }
}
