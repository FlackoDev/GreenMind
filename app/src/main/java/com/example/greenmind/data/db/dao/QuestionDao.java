package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.resource.model.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionDao {

    public static final String COL_ID = "id";
    public static final String COL_QUIZ_ID = "quizId";
    public static final String COL_TEXT = "text";
    public static final String COL_EXPLANATION = "explanation";

    private final DBHelper dbHelper;

    public QuestionDao(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public List<Question> getByQuizId(int quizId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Question> result = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(
                    DBHelper.T_QUESTION,
                    null,
                    COL_QUIZ_ID + "=?",
                    new String[]{String.valueOf(quizId)},
                    null, null, null
            );
            while (c.moveToNext()) {
                result.add(fromCursor(c));
            }
            return result;
        } finally {
            if (c != null) c.close();
        }
    }

    private Question fromCursor(Cursor c) {
        Question q = new Question();
        q.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        q.setQuizId(c.getInt(c.getColumnIndexOrThrow(COL_QUIZ_ID)));
        q.setText(c.getString(c.getColumnIndexOrThrow(COL_TEXT)));
        q.setExplanation(c.getString(c.getColumnIndexOrThrow(COL_EXPLANATION)));
        return q;
    }
}
