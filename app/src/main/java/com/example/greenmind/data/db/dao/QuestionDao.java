package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.model.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionDao {

    private final DBHelper dbHelper;

    public QuestionDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long upsert(Question q) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", q.getId());
        cv.put("quizId", q.getQuizId());
        cv.put("text", q.getText());

        return db.insertWithOnConflict(
                DBHelper.T_QUESTION,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public List<Question> getByQuizId(int quizId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Question> list = new ArrayList<>();

        Cursor c = db.query(
                DBHelper.T_QUESTION,
                null,
                "quizId=?",
                new String[]{String.valueOf(quizId)},
                null, null, null
        );

        while (c.moveToNext()) {
            list.add(new Question(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    quizId,
                    c.getString(c.getColumnIndexOrThrow("text"))
            ));
        }
        c.close();
        return list;
    }
}
