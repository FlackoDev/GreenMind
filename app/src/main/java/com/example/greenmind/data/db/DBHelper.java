package com.example.greenmind.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Calendar;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "greenmind.db";
    public static final int DB_VERSION = 14;

    // ----- TABLE NAMES -----
    public static final String T_QUIZ = "Quiz";
    public static final String T_QUESTION = "Question";
    public static final String T_ANSWER_OPTION = "AnswerOption";
    public static final String T_USER = "User";
    public static final String T_USER_STATS = "UserStats";
    public static final String T_QUIZ_RESULT = "QuizResult";
    public static final String T_LEARNING_CONTENT = "LearningContent";
    public static final String T_BADGE = "Badge";
    public static final String T_LEVEL = "Level";
    public static final String T_LEADERBOARD = "LeaderboardEntry";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_QUIZ + " (" +
                "id INTEGER PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "category TEXT, " +
                "difficulty TEXT, " +
                "points INTEGER DEFAULT 0, " +
                "numQuestions INTEGER DEFAULT 0" +
                ");");

        db.execSQL("CREATE TABLE " + T_USER + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "passwordHash TEXT NOT NULL, " +
                "failedAttempts INTEGER DEFAULT 0, " +
                "lockoutUntil INTEGER DEFAULT 0, " +
                "createdAt INTEGER DEFAULT 0" +
                ");");

        db.execSQL("CREATE TABLE " + T_LEARNING_CONTENT + " (" +
                "id INTEGER PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "category TEXT, " +
                "readingTimeMin INTEGER NOT NULL DEFAULT 0" +
                ");");

        db.execSQL("CREATE TABLE " + T_BADGE + " (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "requiredPoints INTEGER NOT NULL DEFAULT 0" +
                ");");

        db.execSQL("CREATE TABLE " + T_LEVEL + " (" +
                "id INTEGER PRIMARY KEY, " +
                "number INTEGER NOT NULL, " +
                "requiredPoints INTEGER NOT NULL DEFAULT 0" +
                ");");

        db.execSQL("CREATE TABLE " + T_QUESTION + " (" +
                "id INTEGER PRIMARY KEY, " +
                "quizId INTEGER NOT NULL, " +
                "text TEXT NOT NULL, " +
                "explanation TEXT, " +
                "FOREIGN KEY(quizId) REFERENCES " + T_QUIZ + "(id) ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + T_ANSWER_OPTION + " (" +
                "id INTEGER PRIMARY KEY, " +
                "questionId INTEGER NOT NULL, " +
                "text TEXT NOT NULL, " +
                "isCorrect INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(questionId) REFERENCES " + T_QUESTION + "(id) ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + T_USER_STATS + " (" +
                "userId INTEGER PRIMARY KEY, " +
                "totalQuizzes INTEGER NOT NULL DEFAULT 0, " +
                "totalPoints INTEGER NOT NULL DEFAULT 0, " +
                "weeklyChangePerc REAL NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(userId) REFERENCES " + T_USER + "(id) ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + T_QUIZ_RESULT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER NOT NULL, " +
                "quizId INTEGER NOT NULL, " +
                "score INTEGER NOT NULL DEFAULT 0, " +
                "date INTEGER NOT NULL, " +
                "FOREIGN KEY(userId) REFERENCES " + T_USER + "(id) ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY(quizId) REFERENCES " + T_QUIZ + "(id) ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + T_LEADERBOARD + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER NOT NULL, " +
                "points INTEGER NOT NULL DEFAULT 0, " +
                "position INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(userId) REFERENCES " + T_USER + "(id) ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // 1. BADGES (senza \n per evitare problemi di visualizzazione)
        db.execSQL("INSERT INTO " + T_BADGE + " (id, name, description, requiredPoints) VALUES " +
                "(1, 'GREEN SCOUT', 'Primi passi', 100), " +
                "(2, 'NATURE LOVER', 'Appassionato', 250), " +
                "(3, 'ECO WARRIOR', 'Guerriero', 500), " +
                "(4, 'RECYCLE MASTER', 'Maestro del riciclo', 1000);");

        // 2. QUIZ
        db.execSQL("INSERT INTO " + T_QUIZ + " (id, title, category, difficulty, points, numQuestions) VALUES " +
                "(1, 'Quiz Rifiuti Base', 'Gestione Rifiuti', 'Facile', 100, 2);");
        db.execSQL("INSERT INTO " + T_QUIZ + " (id, title, category, difficulty, points, numQuestions) VALUES " +
                "(2, 'Cambiamento Climatico', 'Emergenze', 'Medio', 200, 2);");

        // 3. UTENTI
        String hashedPw = BCrypt.hashpw("password123", BCrypt.gensalt());
        long now = System.currentTimeMillis();
        
        // Account Rachele (id 3)
        db.execSQL("INSERT INTO " + T_USER + " (id, name, email, passwordHash, createdAt) VALUES (3, 'Rachele', 'rachele@gmail.com', '" + hashedPw + "', " + now + ");");
        db.execSQL("INSERT INTO " + T_USER_STATS + " (userId, totalQuizzes, totalPoints, weeklyChangePerc) VALUES (3, 15, 650, 15.5);");

        // 4. RISULTATI PER IL GRAFICO (Account 3)
        Calendar cal = Calendar.getInstance();
        long t0 = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1); long t1 = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -2); long t2 = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1); long t3 = cal.getTimeInMillis();

        db.execSQL("INSERT INTO " + T_QUIZ_RESULT + " (userId, quizId, score, date) VALUES (3, 1, 150, " + t0 + ");");
        db.execSQL("INSERT INTO " + T_QUIZ_RESULT + " (userId, quizId, score, date) VALUES (3, 1, 80, " + t1 + ");");
        db.execSQL("INSERT INTO " + T_QUIZ_RESULT + " (userId, quizId, score, date) VALUES (3, 2, 200, " + t2 + ");");
        db.execSQL("INSERT INTO " + T_QUIZ_RESULT + " (userId, quizId, score, date) VALUES (3, 2, 50, " + t3 + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_ANSWER_OPTION);
        db.execSQL("DROP TABLE IF EXISTS " + T_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + T_QUIZ_RESULT);
        db.execSQL("DROP TABLE IF EXISTS " + T_USER_STATS);
        db.execSQL("DROP TABLE IF EXISTS " + T_LEADERBOARD);
        db.execSQL("DROP TABLE IF EXISTS " + T_USER);
        db.execSQL("DROP TABLE IF EXISTS " + T_QUIZ);
        db.execSQL("DROP TABLE IF EXISTS " + T_BADGE);
        db.execSQL("DROP TABLE IF EXISTS " + T_LEARNING_CONTENT);
        db.execSQL("DROP TABLE IF EXISTS " + T_LEVEL);
        onCreate(db);
    }
}
