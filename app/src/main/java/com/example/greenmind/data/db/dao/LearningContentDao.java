package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.resource.model.LearningContent;

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
        if (c.getId() > 0) cv.put("id", c.getId());
        cv.put("title", c.getTitle());
        cv.put("category", c.getCategory());
        cv.put("readingTimeMin", c.getReadingTimeMin());
        cv.put("preview", c.getPreview());
        cv.put("content", c.getContent());

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
            list.add(mapCursor(c));
        }
        c.close();
        return list;
    }

    public LearningContent getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.T_LEARNING_CONTENT, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        LearningContent item = null;
        if (c.moveToFirst()) {
            item = mapCursor(c);
        }
        c.close();
        return item;
    }

    private LearningContent mapCursor(Cursor c) {
        return new LearningContent(
                c.getInt(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("title")),
                c.getString(c.getColumnIndexOrThrow("category")),
                c.getInt(c.getColumnIndexOrThrow("readingTimeMin")),
                c.getString(c.getColumnIndexOrThrow("preview")),
                c.getString(c.getColumnIndexOrThrow("content"))
        );
    }

    public void insert(LearningContent c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", c.getTitle());
        values.put("category", c.getCategory());
        values.put("readingTimeMin", c.getReadingTimeMin());
        values.put("preview", c.getPreview());
        values.put("content", c.getContent());
        db.insert(DBHelper.T_LEARNING_CONTENT, null, values);
    }
}
