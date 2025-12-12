package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.model.AnswerOption;

import java.util.ArrayList;
import java.util.List;

public class AnswerOptionDao {

    private final DBHelper dbHelper;

    public AnswerOptionDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long upsert(AnswerOption a) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", a.getId());
        cv.put("questionId", a.getQuestionId());
        cv.put("text", a.getText());
        cv.put("isCorrect", a.isCorrect() ? 1 : 0);

        return db.insertWithOnConflict(
                DBHelper.T_ANSWER_OPTION,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public List<AnswerOption> getByQuestionId(int questionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<AnswerOption> list = new ArrayList<>();

        Cursor c = db.query(
                DBHelper.T_ANSWER_OPTION,
                null,
                "questionId=?",
                new String[]{String.valueOf(questionId)},
                null, null, null
        );

        while (c.moveToNext()) {
            list.add(new AnswerOption(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    questionId,
                    c.getString(c.getColumnIndexOrThrow("text")),
                    c.getInt(c.getColumnIndexOrThrow("isCorrect")) == 1
            ));
        }
        c.close();
        return list;
    }
}
