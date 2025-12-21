package com.example.greenmind.data.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.resource.model.AnswerOption;

import java.util.ArrayList;
import java.util.List;

public class AnswerOptionDao {

    public static final String COL_ID = "id";
    public static final String COL_QUESTION_ID = "questionId";
    public static final String COL_TEXT = "text";
    public static final String COL_IS_CORRECT = "isCorrect";

    private final DBHelper dbHelper;

    public AnswerOptionDao(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public List<AnswerOption> getByQuestionId(int questionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<AnswerOption> result = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(
                    DBHelper.T_ANSWER_OPTION,
                    null,
                    COL_QUESTION_ID + "=?",
                    new String[]{String.valueOf(questionId)},
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

    private AnswerOption fromCursor(Cursor c) {
        AnswerOption ao = new AnswerOption();
        ao.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        ao.setQuestionId(c.getInt(c.getColumnIndexOrThrow(COL_QUESTION_ID)));
        ao.setText(c.getString(c.getColumnIndexOrThrow(COL_TEXT)));
        ao.setCorrect(c.getInt(c.getColumnIndexOrThrow(COL_IS_CORRECT)) == 1);
        return ao;
    }
}
