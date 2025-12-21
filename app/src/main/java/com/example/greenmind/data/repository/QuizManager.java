package com.example.greenmind.data.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.data.db.dao.QuizDao;
import com.example.greenmind.resource.model.Quiz;

import java.util.Calendar;

public class QuizManager {

    private final Context context;
    private final QuizDao quizDao;
    private final DBHelper dbHelper;

    // Data di riferimento: 1 Gennaio 2024 (puoi cambiarla con quella che preferisci)
    private static final long START_DATE_MILLIS = 1704063600000L; 

    public QuizManager(Context context) {
        this.context = context;
        this.quizDao = new QuizDao(context);
        this.dbHelper = new DBHelper(context);
    }

    /**
     * Calcola l'ID del quiz per oggi.
     * Rotazione basata sul numero di giorni trascorsi dalla START_DATE.
     */
    public int getDailyQuizId() {
        long today = System.currentTimeMillis();
        long diff = today - START_DATE_MILLIS;
        
        // Se per qualche motivo la data di sistema è prima della data di inizio
        if (diff < 0) diff = 0;

        int daysPassed = (int) (diff / (24 * 60 * 60 * 1000));
        
        // Supponiamo di avere 30 quiz. Ruotiamo tra 1 e 30.
        return (daysPassed % 30) + 1;
    }

    /**
     * Recupera il quiz di oggi dal Database.
     */
    public Quiz getDailyQuiz() {
        int quizId = getDailyQuizId();
        return quizDao.getById(quizId);
    }

    /**
     * Controlla se l'utente ha già completato un determinato quiz.
     */
    public boolean isQuizCompleted(int userId, int quizId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.T_QUIZ_RESULT + 
                            " WHERE userId = ? AND quizId = ?", 
                            new String[]{String.valueOf(userId), String.valueOf(quizId)});
            if (c.moveToFirst()) {
                return c.getInt(0) > 0;
            }
            return false;
        } finally {
            if (c != null) c.close();
        }
    }
}
