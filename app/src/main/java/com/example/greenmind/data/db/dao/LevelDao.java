package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.model.Level;

public class LevelDao {

    private final DBHelper dbHelper;

    public LevelDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long upsert(Level l) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", l.getId());
        cv.put("number", l.getNumber());
        cv.put("requiredPoints", l.getRequiredPoints());

        return db.insertWithOnConflict(
                DBHelper.T_LEVEL,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }
}
