package com.example.greenmind.data.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.data.db.dao.QuizDao;
import com.example.greenmind.resource.model.Quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizManager {

    private final Context context;
    private final QuizDao quizDao;
    private final DBHelper dbHelper;

    // Data di riferimento: 1 Gennaio 2024
    private static final long START_DATE_MILLIS = 1704063600000L;

    public QuizManager(Context context) {
        this.context = context;
        this.quizDao = new QuizDao(context);
        this.dbHelper = new DBHelper(context);
    }

    /**
     * Calcola gli ID dei 3 quiz per oggi.
     * Rotazione basata sul numero di giorni trascorsi dalla START_DATE.
     */
    public List<Integer> getDailyQuizIds() {
        long today = System.currentTimeMillis();
        long diff = today - START_DATE_MILLIS;
        if (diff < 0) diff = 0;

        int daysPassed = (int) (diff / (24 * 60 * 60 * 1000));
        int totalQuizzes = quizDao.getCount();
        
        List<Integer> dailyIds = new ArrayList<>();
        if (totalQuizzes == 0) return dailyIds;

        // Se abbiamo meno di 3 quiz, li prendiamo tutti
        if (totalQuizzes <= 3) {
            for (int i = 1; i <= totalQuizzes; i++) {
                dailyIds.add(i);
            }
            return dailyIds;
        }

        // Logica di rotazione: ogni giorno saltiamo di 3
        int startIndex = (daysPassed * 3) % totalQuizzes;
        
        // Recuperiamo tutti gli ID disponibili (assumendo ID sequenziali o quasi)
        // Per sicurezza, potremmo recuperare tutti i quiz e poi scegliere
        List<Quiz> all = quizDao.getAll();
        for (int i = 0; i < 3; i++) {
            int index = (startIndex + i) % all.size();
            dailyIds.add(all.get(index).getId());
        }

        return dailyIds;
    }

    /**
     * Recupera i 3 quiz di oggi dal Database.
     */
    public List<Quiz> getDailyQuizzes() {
        List<Integer> ids = getDailyQuizIds();
        return quizDao.getByIds(ids);
    }

    /**
     * Recupera il quiz con più punti tra quelli del giorno (per la Home).
     */
    public Quiz getFeaturedDailyQuiz() {
        List<Quiz> dailyQuizzes = getDailyQuizzes();
        if (dailyQuizzes.isEmpty()) return null;

        Quiz featured = dailyQuizzes.get(0);
        for (Quiz q : dailyQuizzes) {
            if (q.getPoints() > featured.getPoints()) {
                featured = q;
            }
        }
        return featured;
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
