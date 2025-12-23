package com.example.greenmind.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.mindrot.jbcrypt.BCrypt;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "greenmind.db";
    public static final int DB_VERSION = 26;

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
        db.execSQL("CREATE TABLE " + T_QUIZ + " (id INTEGER PRIMARY KEY, title TEXT NOT NULL, category TEXT, difficulty TEXT, points INTEGER DEFAULT 0, numQuestions INTEGER DEFAULT 0);");
        db.execSQL("CREATE TABLE " + T_USER + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, email TEXT NOT NULL UNIQUE, passwordHash TEXT NOT NULL, failedAttempts INTEGER DEFAULT 0, lockoutUntil INTEGER DEFAULT 0, createdAt INTEGER DEFAULT 0);");
        db.execSQL("CREATE TABLE " + T_LEARNING_CONTENT + " (id INTEGER PRIMARY KEY, title TEXT NOT NULL, category TEXT, readingTimeMin INTEGER NOT NULL DEFAULT 0, preview TEXT, content TEXT);");
        db.execSQL("CREATE TABLE " + T_BADGE + " (id INTEGER PRIMARY KEY, name TEXT NOT NULL, description TEXT, requiredPoints INTEGER NOT NULL DEFAULT 0);");
        db.execSQL("CREATE TABLE " + T_LEVEL + " (id INTEGER PRIMARY KEY, number INTEGER NOT NULL, requiredPoints INTEGER NOT NULL DEFAULT 0);");
        db.execSQL("CREATE TABLE " + T_QUESTION + " (id INTEGER PRIMARY KEY, quizId INTEGER NOT NULL, text TEXT NOT NULL, explanation TEXT, FOREIGN KEY(quizId) REFERENCES " + T_QUIZ + "(id) ON UPDATE CASCADE ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE " + T_ANSWER_OPTION + " (id INTEGER PRIMARY KEY, questionId INTEGER NOT NULL, text TEXT NOT NULL, isCorrect INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(questionId) REFERENCES " + T_QUESTION + "(id) ON UPDATE CASCADE ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE " + T_USER_STATS + " (userId INTEGER PRIMARY KEY, totalQuizzes INTEGER NOT NULL DEFAULT 0, totalPoints INTEGER NOT NULL DEFAULT 0, weeklyChangePerc REAL NOT NULL DEFAULT 0, FOREIGN KEY(userId) REFERENCES " + T_USER + "(id) ON UPDATE CASCADE ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE " + T_QUIZ_RESULT + " (id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER NOT NULL, quizId INTEGER NOT NULL, score INTEGER NOT NULL DEFAULT 0, date INTEGER NOT NULL, FOREIGN KEY(userId) REFERENCES " + T_USER + "(id) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY(quizId) REFERENCES " + T_QUIZ + "(id) ON UPDATE CASCADE ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE " + T_LEADERBOARD + " (id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER NOT NULL, points INTEGER NOT NULL DEFAULT 0, position INTEGER NOT NULL DEFAULT 0, FOREIGN KEY(userId) REFERENCES " + T_USER + "(id) ON UPDATE CASCADE ON DELETE CASCADE);");

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // 1. BADGES
        db.execSQL("INSERT INTO " + T_BADGE + " (id, name, description, requiredPoints) VALUES (1, 'GREEN SCOUT', 'Primi passi', 100), (2, 'NATURE LOVER', 'Appassionato', 250), (3, 'ECO WARRIOR', 'Guerriero', 500), (4, 'RECYCLE MASTER', 'Maestro del riciclo', 1000), (5, 'EARTH GUARDIAN', 'Protettore della terra', 2500), (6, 'PLANET SAVIOR', 'Salvatore del pianeta', 5000), (7, 'CLIMATE HERO', 'Eroe del clima', 10000), (8, 'GREEN LEGEND', 'Leggenda verde', 25000), (9, 'ECO MESSIAH', 'Il messia dell''ecologia', 100000);");
        
        // 2. LEARNING CONTENT
        db.execSQL("INSERT INTO " + T_LEARNING_CONTENT + " (id, title, category, readingTimeMin, preview, content) VALUES " +
                "(1, 'Come gestire le emergenze climatiche', 'EMERGENZE CLIMATICHE', 5, 'Le ondate di calore e le inondazioni sono sempre più frequenti. Ecco una guida pratica su cosa fare in caso...', 'Le ondate di calore e le inondazioni sono sempre più frequenti a causa del riscaldamento globale. È fondamentale essere preparati.\\n\\nIn caso di ondate di calore:\\n1. Evita di uscire nelle ore più calde (dalle 11 alle 18).\\n2. Bevi molta acqua, anche se non senti sete.\\n3. Mantieni la casa fresca chiudendo le tapparelle durante il giorno.\\n4. Indossa abiti leggeri e di fibre naturali come cotone o lino.\\n\\nIn caso di inondazioni:\\n1. Sali ai piani superiori e non scendere mai in cantina o garage.\\n2. Chiudi l''interruttore generale della corrente elettrica e del gas.\\n3. Segui le istruzioni delle autorità via radio o social media.\\n4. Non tentare di attraversare zone allagate a piedi o in auto, la forza dell''acqua può essere ingannevole.\\n\\nLa prevenzione è la chiave per ridurre i rischi legati ai cambiamenti climatici estremi. Investire in infrastrutture resilienti e informare la popolazione sono passi cruciali per un futuro più sicuro per tutti.'), " +
                "(2, 'Guida alla raccolta differenziata', 'GESTIONE RIFIUTI', 3, 'Separare correttamente i rifiuti è il primo passo per un futuro sostenibile. Scopri dove buttare il Tetra Pak e...', 'Separare correttamente i rifiuti è il primo passo fondamentale per un futuro sostenibile e per favorire l''economia circolare.\\n\\nEcco alcuni errori comuni da evitare:\\n1. Il Tetra Pak: in molti comuni va con la carta, in altri con la plastica. Controlla sempre le disposizioni locali.\\n2. Scontrini: non vanno nella carta! Sono fatti di carta termica che non è riciclabile con la carta normale. Vanno nel secco residuo.\\n3. Ceramica e Pyrex: non sono vetro! Se rompi un piatto o una pirofila, non metterli nel contenitore del vetro, ma nel secco.\\n4. Plastica sporca: i contenitori per alimenti devono essere svuotati e sciacquati grossolanamente prima di essere riciclati.\\n\\nPerché riciclare?\\nIl riciclo riduce l''estrazione di nuove materie prime, risparmia energia e diminuisce le emissioni di gas serra prodotte dalle discariche. Ogni piccolo gesto conta per proteggere il nostro pianeta. Ricordati che la gerarchia dei rifiuti mette al primo posto la riduzione della produzione di rifiuti stessi, poi il riuso e infine il riciclo.'), " +
                "(3, 'Il Futuro delle Energie Rinnovabili', 'ENERGIA', 8, 'Sole e vento per il pianeta.', 'Le rinnovabili sono il futuro...'), " +
                "(4, 'Ridurre lo spreco d''acqua', 'CONSUMI', 4, 'Piccoli gesti, grandi risparmi.', 'Chiudi il rubinetto quando puoi...');");

        // 3. QUIZ
        db.execSQL("INSERT INTO " + T_QUIZ + " (id, title, category, difficulty, points, numQuestions) VALUES " +
                "(1, 'Quiz Rifiuti Base', 'Gestione Rifiuti', 'Facile', 100, 2), " +
                "(2, 'Cambiamento Climatico', 'Emergenze', 'Medio', 200, 2), " +
                "(3, 'Risparmio Idrico', 'Sostenibilità', 'Facile', 150, 2), " +
                "(4, 'Energie Rinnovabili', 'Energia', 'Difficile', 300, 2);");

        // 4. DOMANDE
        db.execSQL("INSERT INTO " + T_QUESTION + " (id, quizId, text, explanation) VALUES " +
                "(1, 1, 'Dove va gettato un bicchiere di vetro rotto?', 'Il vetro cristallo va nel secco.'), " +
                "(2, 1, 'Gli scontrini vanno nella carta?', 'No, carta termica va nel secco.'), " +
                "(3, 2, 'Qual è il principale gas serra?', 'CO2 è il principale gas emesso.'), " +
                "(4, 2, 'Cos''è la neutralità carbonica?', 'Bilanciare emissioni e rimozione.'), " +
                "(5, 3, 'Quanta acqua risparmi chiudendo il rubinetto?', 'Circa 6 litri al minuto.'), " +
                "(6, 3, 'Meglio doccia o vasca?', 'La doccia consuma molto meno.'), " +
                "(7, 4, 'Qual è una fonte rinnovabile?', 'Il sole è una fonte inesauribile.'), " +
                "(8, 4, 'L''eolico usa il vento?', 'Sì, trasforma il vento in energia.');");

        // 5. RISPOSTE
        db.execSQL("INSERT INTO " + T_ANSWER_OPTION + " (id, questionId, text, isCorrect) VALUES " +
                "(1, 1, 'Vetro', 0), (2, 1, 'Secco', 1), " +
                "(3, 2, 'Sì', 0), (4, 2, 'No', 1), " +
                "(5, 3, 'Metano', 0), (6, 3, 'CO2', 1), " +
                "(7, 4, 'Non emettere nulla', 0), (8, 4, 'Bilanciare', 1), " +
                "(9, 5, '2 litri', 0), (10, 5, '6 litri', 1), " +
                "(11, 6, 'Doccia', 1), (12, 6, 'Vasca', 0), " +
                "(13, 7, 'Carbone', 0), (14, 7, 'Sole', 1), " +
                "(15, 8, 'Sì', 1), (16, 8, 'No', 0);");

        // 6. UTENTE E STATISTICHE
        String hashedPw = BCrypt.hashpw("password123", BCrypt.gensalt());
        db.execSQL("INSERT INTO " + T_USER + " (id, name, email, passwordHash, createdAt) VALUES (1, 'Rachele', 'rachele@gmail.com', '" + hashedPw + "', " + System.currentTimeMillis() + ");");
        db.execSQL("INSERT INTO " + T_USER_STATS + " (userId, totalQuizzes, totalPoints, weeklyChangePerc) VALUES (1, 42, 1250, 12);");
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
