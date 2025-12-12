package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.model.LearningContent;

import java.util.ArrayList;
import java.util.List;

public class LearningContentDao {

    private final DBHelper dbHelper;

    public LearningContentDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long upsert(LearningContent c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", c.getId());
        cv.put("title", c.getTitle());
        cv.put("category", c.getCategory());
        cv.put("readingTimeMin", c.getReadingTimeMin());

        return db.insertWithOnConflict(
                DBHelper.T_LEARNING_CONTENT,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public List<LearningContent> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<LearningContent> list = new ArrayList<>();

        Cursor c = db.query(DBHelper.T_LEARNING_CONTENT, null, null, null, null, null, null);
        while (c.moveToNext()) {
            list.add(new LearningContent(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("title")),
                    c.getString(c.getColumnIndexOrThrow("category")),
                    c.getInt(c.getColumnIndexOrThrow("readingTimeMin"))
            ));
        }
        c.close();
        return list;
    }
}
