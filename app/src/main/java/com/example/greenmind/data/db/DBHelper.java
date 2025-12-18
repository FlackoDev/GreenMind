package com.example.greenmind.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.mindrot.jbcrypt.BCrypt;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "greenmind.db";
    public static final int DB_VERSION = 6;

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
        // Parent tables
        db.execSQL("CREATE TABLE " + T_QUIZ + " (" +
                "id INTEGER PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "category TEXT, " +
                "difficulty TEXT" +
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

        // Child tables
        db.execSQL("CREATE TABLE " + T_QUESTION + " (" +
                "id INTEGER PRIMARY KEY, " +
                "quizId INTEGER NOT NULL, " +
                "text TEXT NOT NULL, " +
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

        // Indexes
        db.execSQL("CREATE INDEX idx_question_quizId ON " + T_QUESTION + "(quizId);");
        db.execSQL("CREATE INDEX idx_answeroption_questionId ON " + T_ANSWER_OPTION + "(questionId);");
        db.execSQL("CREATE INDEX idx_quizresult_userId ON " + T_QUIZ_RESULT + "(userId);");
        db.execSQL("CREATE INDEX idx_quiz_category ON " + T_QUIZ + "(category);");

        // Popolamento dati iniziali
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // 1. 10 Badge di esempio
        db.execSQL("INSERT INTO " + T_BADGE + " (id, name, description, requiredPoints) VALUES " +
                "(1, 'GREEN SCOUT', 'Primi passi verso la sostenibilità', 100), " +
                "(2, 'NATURE LOVER', 'Appassionato della biodiversità', 250), " +
                "(3, 'ECO WARRIOR', 'Guerriero per un mondo più pulito', 500), " +
                "(4, 'EARTH SAVER', 'Protettore attivo del pianeta', 750), " +
                "(5, 'RECYCLE MASTER', 'Maestro del riciclo creativo', 1000), " +
                "(6, 'SUSTAINABILITY HERO', 'Eroe della vita sostenibile', 1500), " +
                "(7, 'CLIMATE GUARDIAN', 'Custode del clima globale', 2000), " +
                "(8, 'FOREST PROTECTOR', 'Difensore dei polmoni verdi', 3000), " +
                "(9, 'OCEAN DEFENDER', 'Protettore delle acque e dei mari', 4000), " +
                "(10, 'PLANET CHAMPION', 'Campione assoluto della Terra', 5000);");

        // 2. Utenti di esempio (password: password123)
        String hashedPw = BCrypt.hashpw("password123", BCrypt.gensalt());
        long now = System.currentTimeMillis();

        ContentValues user1 = new ContentValues();
        user1.put("name", "Ale");
        user1.put("email", "ale@gmail.com");
        user1.put("passwordHash", hashedPw);
        user1.put("createdAt", now);
        long u1Id = db.insert(T_USER, null, user1);

        ContentValues user2 = new ContentValues();
        user2.put("name", "Rachele");
        user2.put("email", "rachele@gmail.com");
        user2.put("passwordHash", hashedPw);
        user2.put("createdAt", now);
        long u2Id = db.insert(T_USER, null, user2);

        // 3. Statistiche per gli utenti
        if (u1Id != -1) {
            db.execSQL("INSERT INTO " + T_USER_STATS + " (userId, totalQuizzes, totalPoints, weeklyChangePerc) VALUES (" + u1Id + ", 42, 1250, 12.5);");
        }
        if (u2Id != -1) {
            db.execSQL("INSERT INTO " + T_USER_STATS + " (userId, totalQuizzes, totalPoints, weeklyChangePerc) VALUES (" + u2Id + ", 15, 450, -2.0);");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Reset totale per sviluppo
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
