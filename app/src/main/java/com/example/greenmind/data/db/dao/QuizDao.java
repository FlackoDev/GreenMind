package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.resource.model.Quiz;

import java.util.ArrayList;
import java.util.List;

public class QuizDao {

    // ---- colonne ----
    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_CATEGORY = "category";
    public static final String COL_DIFFICULTY = "difficulty";
    public static final String COL_POINTS = "points";
    public static final String COL_NUM_QUESTIONS = "numQuestions";

    private final DBHelper dbHelper;

    public QuizDao(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public long upsert(Quiz quiz) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return upsert(db, quiz);
    }

    public long upsert(SQLiteDatabase db, Quiz quiz) {
        ContentValues cv = toContentValues(quiz);
        return db.insertWithOnConflict(
                DBHelper.T_QUIZ,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public Quiz getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(
                    DBHelper.T_QUIZ,
                    null,
                    COL_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null
            );

            if (c.moveToFirst()) {
                return fromCursor(c);
            }
            return null;
        } finally {
            if (c != null) c.close();
        }
    }

    public List<Quiz> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Quiz> result = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(
                    DBHelper.T_QUIZ,
                    null,
                    null, null,
                    null, null,
                    COL_TITLE + " ASC"
            );
            while (c.moveToNext()) {
                result.add(fromCursor(c));
            }
            return result;
        } finally {
            if (c != null) c.close();
        }
    }

    public int deleteById(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DBHelper.T_QUIZ, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    private ContentValues toContentValues(Quiz quiz) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, quiz.getId());
        cv.put(COL_TITLE, quiz.getTitle());
        cv.put(COL_CATEGORY, quiz.getCategory());
        cv.put(COL_DIFFICULTY, quiz.getDifficulty());
        cv.put(COL_POINTS, quiz.getPoints());
        cv.put(COL_NUM_QUESTIONS, quiz.getNumQuestions());
        return cv;
    }

    private Quiz fromCursor(Cursor c) {
        Quiz q = new Quiz();
        q.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        q.setTitle(c.getString(c.getColumnIndexOrThrow(COL_TITLE)));
        q.setCategory(c.getString(c.getColumnIndexOrThrow(COL_CATEGORY)));
        q.setDifficulty(c.getString(c.getColumnIndexOrThrow(COL_DIFFICULTY)));
        q.setPoints(c.getInt(c.getColumnIndexOrThrow(COL_POINTS)));
        q.setNumQuestions(c.getInt(c.getColumnIndexOrThrow(COL_NUM_QUESTIONS)));
        return q;
    }
}
