package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.resource.model.Badge;

import java.util.ArrayList;
import java.util.List;

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

    public List<Badge> getAllBadges() {
        List<Badge> badges = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.T_BADGE, null, null, null, null, null, "requiredPoints ASC");
        
        if (c != null && c.moveToFirst()) {
            do {
                Badge b = new Badge(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getString(c.getColumnIndexOrThrow("description")),
                    c.getInt(c.getColumnIndexOrThrow("requiredPoints"))
                );
                badges.add(b);
            } while (c.moveToNext());
            c.close();
        }
        return badges;
    }
}
