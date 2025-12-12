package com.example.greenmind.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "greenmind.db";
    public static final int DB_VERSION = 1;

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

    /** Abilita realmente le FK su Android */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // API 16+
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // =========================
        // 1) TABLES "PARENT" / STATIC
        // =========================
        db.execSQL("CREATE TABLE " + T_QUIZ + " (" +
                "id INTEGER PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "category TEXT, " +
                "difficulty TEXT" +
                ");");

        db.execSQL("CREATE TABLE " + T_USER + " (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "passwordHash TEXT NOT NULL" +
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

        // =========================
        // 2) CHILD TABLES (FK)
        // =========================

        // Question -> Quiz
        db.execSQL("CREATE TABLE " + T_QUESTION + " (" +
                "id INTEGER PRIMARY KEY, " +
                "quizId INTEGER NOT NULL, " +
                "text TEXT NOT NULL, " +
                "FOREIGN KEY(quizId) REFERENCES " + T_QUIZ + "(id) " +
                "ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");

        // AnswerOption -> Question
        // (Modello consigliato: la correttezza è per opzione, non per domanda)
        db.execSQL("CREATE TABLE " + T_ANSWER_OPTION + " (" +
                "id INTEGER PRIMARY KEY, " +
                "questionId INTEGER NOT NULL, " +
                "text TEXT NOT NULL, " +
                "isCorrect INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(questionId) REFERENCES " + T_QUESTION + "(id) " +
                "ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");

        // UserStats (1-1) -> User
        db.execSQL("CREATE TABLE " + T_USER_STATS + " (" +
                "userId INTEGER PRIMARY KEY, " +
                "totalQuizzes INTEGER NOT NULL DEFAULT 0, " +
                "totalPoints INTEGER NOT NULL DEFAULT 0, " +
                "weeklyChangePerc REAL NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(userId) REFERENCES " + T_USER + "(id) " +
                "ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");

        // QuizResult -> User, Quiz
        // date: timestamp long (INTEGER)
        db.execSQL("CREATE TABLE " + T_QUIZ_RESULT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER NOT NULL, " +
                "quizId INTEGER NOT NULL, " +
                "score INTEGER NOT NULL DEFAULT 0, " +
                "date INTEGER NOT NULL, " +
                "FOREIGN KEY(userId) REFERENCES " + T_USER + "(id) " +
                "ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY(quizId) REFERENCES " + T_QUIZ + "(id) " +
                "ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");

        // LeaderboardEntry -> User
        db.execSQL("CREATE TABLE " + T_LEADERBOARD + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER NOT NULL, " +
                "points INTEGER NOT NULL DEFAULT 0, " +
                "position INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(userId) REFERENCES " + T_USER + "(id) " +
                "ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");

        // =========================
        // 3) INDEXES (FK + query speed)
        // =========================
        db.execSQL("CREATE INDEX idx_question_quizId ON " + T_QUESTION + "(quizId);");
        db.execSQL("CREATE INDEX idx_answeroption_questionId ON " + T_ANSWER_OPTION + "(questionId);");

        db.execSQL("CREATE INDEX idx_quizresult_userId ON " + T_QUIZ_RESULT + "(userId);");
        db.execSQL("CREATE INDEX idx_quizresult_quizId ON " + T_QUIZ_RESULT + "(quizId);");

        db.execSQL("CREATE INDEX idx_leaderboard_userId ON " + T_LEADERBOARD + "(userId);");
        db.execSQL("CREATE INDEX idx_learning_category ON " + T_LEARNING_CONTENT + "(category);");
        db.execSQL("CREATE INDEX idx_quiz_category ON " + T_QUIZ + "(category);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop in ordine "figli -> padri" per evitare errori con FK
        db.execSQL("DROP TABLE IF EXISTS " + T_LEADERBOARD);
        db.execSQL("DROP TABLE IF EXISTS " + T_QUIZ_RESULT);
        db.execSQL("DROP TABLE IF EXISTS " + T_USER_STATS);
        db.execSQL("DROP TABLE IF EXISTS " + T_ANSWER_OPTION);
        db.execSQL("DROP TABLE IF EXISTS " + T_QUESTION);

        db.execSQL("DROP TABLE IF EXISTS " + T_LEVEL);
        db.execSQL("DROP TABLE IF EXISTS " + T_BADGE);
        db.execSQL("DROP TABLE IF EXISTS " + T_LEARNING_CONTENT);
        db.execSQL("DROP TABLE IF EXISTS " + T_USER);
        db.execSQL("DROP TABLE IF EXISTS " + T_QUIZ);

        onCreate(db);
    }
}
