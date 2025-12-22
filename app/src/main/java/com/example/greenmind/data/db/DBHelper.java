package com.example.greenmind.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.mindrot.jbcrypt.BCrypt;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "greenmind.db";
    public static final int DB_VERSION = 13; // Incrementato per aggiornamento contenuti

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
                "preview TEXT, " +
                "content TEXT, " +
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

        db.execSQL("CREATE INDEX idx_question_quizId ON " + T_QUESTION + "(quizId);");
        db.execSQL("CREATE INDEX idx_answeroption_questionId ON " + T_ANSWER_OPTION + "(questionId);");
        db.execSQL("CREATE INDEX idx_quizresult_userId ON " + T_QUIZ_RESULT + "(userId);");
        db.execSQL("CREATE INDEX idx_quiz_category ON " + T_QUIZ + "(category);");

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // --- BADGES ---
        db.execSQL("INSERT INTO " + T_BADGE + " (id, name, description, requiredPoints) VALUES " +
                "(1, 'GREEN SCOUT', 'Primi passi verso la sostenibilità', 100), " +
                "(2, 'NATURE LOVER', 'Appassionato della biodiversità', 250), " +
                "(3, 'ECO WARRIOR', 'Guerriero per un mondo più pulito', 500);");

        // --- UTENTI ---
        String hashedPw = BCrypt.hashpw("password123", BCrypt.gensalt());
        long now = System.currentTimeMillis();
        db.execSQL("INSERT INTO " + T_USER + " (id, name, email, passwordHash, createdAt) VALUES " +
                "(1, 'Ale', 'ale@gmail.com', '" + hashedPw + "', " + now + ");");
        db.execSQL("INSERT INTO " + T_USER_STATS + " (userId, totalQuizzes, totalPoints, weeklyChangePerc) VALUES (1, 0, 0, 0);");

        // --- LEARNING CONTENTS ---
        db.execSQL("INSERT INTO " + T_LEARNING_CONTENT + " (id, title, category, preview, content, readingTimeMin) VALUES " +
                "(1, 'L''arte del riciclo creativo', 'Sostenibilità', 'Scopri come trasformare i rifiuti in oggetti utili.', " +
                "'Il riciclo creativo è una pratica che permette di dare nuova vita a oggetti che altrimenti finirebbero in discarica. Oltre a ridurre l''inquinamento, stimola la creatività.', 5), " +
                "(2, 'Energia Rinnovabile in Casa', 'Ambiente', 'Piccoli passi per ridurre l''impatto energetico domestico.', " +
                "'L''utilizzo di pannelli solari e lampadine a LED può ridurre drasticamente il consumo di energia elettrica. La sostenibilità parte dalle mura di casa nostra.', 8);");

        // --- QUIZ DI ESEMPIO (Giorno 1) ---
        db.execSQL("INSERT INTO " + T_QUIZ + " (id, title, category, difficulty, points, numQuestions) VALUES " +
                "(1, 'Quiz Rifiuti Base', 'Gestione Rifiuti', 'Facile', 100, 2);");

        // Domanda 1
        db.execSQL("INSERT INTO " + T_QUESTION + " (id, quizId, text, explanation) VALUES " +
                "(1, 1, 'Dove va gettato un bicchiere di vetro rotto?', " +
                "'Il vetro cristallo o pyrex ha un punto di fusione diverso dal vetro da imballaggio. Va nel secco.');");
        db.execSQL("INSERT INTO " + T_ANSWER_OPTION + " (id, questionId, text, isCorrect) VALUES " +
                "(1, 1, 'Vetro', 0), (2, 1, 'Secco/Indifferenziato', 1), (3, 1, 'Plastica', 0), (4, 1, 'Umido', 0);");

        // Domanda 2
        db.execSQL("INSERT INTO " + T_QUESTION + " (id, quizId, text, explanation) VALUES " +
                "(2, 1, 'Gli scontrini della spesa vanno gettati nella carta?', " +
                "'No, la maggior parte degli scontrini è fatta di carta termica che reagisce al calore e non può essere riciclata con la carta.');");
        db.execSQL("INSERT INTO " + T_ANSWER_OPTION + " (id, questionId, text, isCorrect) VALUES " +
                "(5, 2, 'Sì', 0), (6, 2, 'No, vanno nel secco', 1);");

        // --- QUIZ DI ESEMPIO (Giorno 2) ---
        db.execSQL("INSERT INTO " + T_QUIZ + " (id, title, category, difficulty, points, numQuestions) VALUES " +
                "(2, 'Cambiamento Climatico', 'Emergenze Climatiche', 'Medio', 200, 2);");

        // Domanda 3
        db.execSQL("INSERT INTO " + T_QUESTION + " (id, quizId, text, explanation) VALUES " +
                "(3, 2, 'Qual è il principale gas serra emesso dalle attività umane?', " +
                "'L''anidride carbonica (CO2) è il principale gas serra derivante dalla combustione di combustibili fossili.');");
        db.execSQL("INSERT INTO " + T_ANSWER_OPTION + " (id, questionId, text, isCorrect) VALUES " +
                "(7, 3, 'Metano', 0), (8, 3, 'Anidride Carbonica', 1), (9, 3, 'Ossigeno', 0);");
        
        // Domanda 4
        db.execSQL("INSERT INTO " + T_QUESTION + " (id, quizId, text, explanation) VALUES " +
                "(4, 2, 'Cosa si intende per Neutralità Carbonica?', " +
                "'Significa bilanciare le emissioni di CO2 con la loro rimozione dall''atmosfera.');");
        db.execSQL("INSERT INTO " + T_ANSWER_OPTION + " (id, questionId, text, isCorrect) VALUES " +
                "(10, 4, 'Non emettere nulla', 0), (11, 4, 'Bilanciare emissioni e rimozione', 1);");
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
