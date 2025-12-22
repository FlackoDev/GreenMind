package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.resource.model.QuizResult;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Ritorna i punti totalizzati negli ultimi 7 giorni (inclusi oggi).
     * La chiave è il giorno della settimana (Calendar.SUNDAY, etc.)
     */
    public Map<Integer, Integer> getPointsLast7Days(int userId) {
        Map<Integer, Integer> pointsPerDay = new HashMap<>();
        
        // Inizializziamo a 0 per sicurezza
        for (int i = 1; i <= 7; i++) {
            pointsPerDay.put(i, 0);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, -6); // 7 giorni fa (incluso oggi)
        
        long startTime = cal.getTimeInMillis();

        String query = "SELECT date, score FROM " + DBHelper.T_QUIZ_RESULT + 
                       " WHERE userId = ? AND date >= ?";
        
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(startTime)});
        
        if (c != null && c.moveToFirst()) {
            do {
                long date = c.getLong(c.getColumnIndexOrThrow("date"));
                int score = c.getInt(c.getColumnIndexOrThrow("score"));
                
                Calendar itemCal = Calendar.getInstance();
                itemCal.setTimeInMillis(date);
                int dayOfWeek = itemCal.get(Calendar.DAY_OF_WEEK);
                
                int currentPoints = pointsPerDay.getOrDefault(dayOfWeek, 0);
                pointsPerDay.put(dayOfWeek, currentPoints + score);
                
            } while (c.moveToNext());
            c.close();
        }
        
        return pointsPerDay;
    }
}
