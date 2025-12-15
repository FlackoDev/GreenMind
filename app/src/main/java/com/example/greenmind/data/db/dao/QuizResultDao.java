package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.model.QuizResult;

public class QuizResultDao {

    private final DBHelper dbHelper;

    public QuizResultDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long insert(QuizResult r) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("userId", r.getUserId());
        cv.put("quizId", r.getQuizId());
        cv.put("score", r.getScore());
        cv.put("date", r.getDate());

        return db.insert(DBHelper.T_QUIZ_RESULT, null, cv);
    }
}
