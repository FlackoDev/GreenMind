package com.example.greenmind.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.resource.model.User;

import org.mindrot.jbcrypt.BCrypt;

public class UserDao {

    private final DBHelper dbHelper;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000;

    public UserDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * Registra un nuovo utente e inizializza le sue statistiche.
     * Utilizza una transazione per garantire l'integrità dei dati.
     */
    public long registerUser(String name, String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long userId = -1;
        db.beginTransaction();
        try {
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

            ContentValues cv = new ContentValues();
            cv.put("name", name);
            cv.put("email", email);
            cv.put("passwordHash", passwordHash);
            cv.put("failedAttempts", 0);
            cv.put("lockoutUntil", 0);
            cv.put("createdAt", System.currentTimeMillis());

            userId = db.insert(DBHelper.T_USER, null, cv);

            if (userId != -1) {
                ContentValues statsCv = new ContentValues();
                statsCv.put("userId", userId);
                statsCv.put("totalQuizzes", 0);
                statsCv.put("totalPoints", 0);
                statsCv.put("weeklyChangePerc", 0);
                
                long statsId = db.insert(DBHelper.T_USER_STATS, null, statsCv);
                if (statsId != -1) {
                    db.setTransactionSuccessful();
                } else {
                    userId = -1; // Se fallisce l'inserimento stats, annulliamo tutto
                }
            }
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    public boolean userExists(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.T_USER, new String[]{"id"}, "id=?", new String[]{String.valueOf(userId)}, null, null, null);
        boolean exists = (c != null && c.getCount() > 0);
        if (c != null) c.close();
        return exists;
    }

    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String passwordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        ContentValues cv = new ContentValues();
        cv.put("passwordHash", passwordHash);
        cv.put("failedAttempts", 0);
        cv.put("lockoutUntil", 0);
        int rows = db.update(DBHelper.T_USER, cv, "email=?", new String[]{email});
        return rows > 0;
    }

    public enum AuthResult {
        SUCCESS,
        INVALID_CREDENTIALS,
        LOCKED
    }

    private User lastAuthenticatedUser;

    public User getLastAuthenticatedUser() {
        return lastAuthenticatedUser;
    }

    public AuthResult authenticate(String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        lastAuthenticatedUser = null;

        Cursor c = db.query(DBHelper.T_USER, null, "email=?", new String[]{email}, null, null, null);
        
        if (c == null || !c.moveToFirst()) {
            if (c != null) c.close();
            return AuthResult.INVALID_CREDENTIALS;
        }

        int id = c.getInt(c.getColumnIndexOrThrow("id"));
        String name = c.getString(c.getColumnIndexOrThrow("name"));
        String hash = c.getString(c.getColumnIndexOrThrow("passwordHash"));
        int failedAttempts = c.getInt(c.getColumnIndexOrThrow("failedAttempts"));
        long lockoutUntil = c.getLong(c.getColumnIndexOrThrow("lockoutUntil"));
        long createdAt = c.getLong(c.getColumnIndexOrThrow("createdAt"));
        c.close();

        long currentTime = System.currentTimeMillis();

        if (lockoutUntil > currentTime) {
            return AuthResult.LOCKED;
        }

        if (BCrypt.checkpw(password, hash)) {
            resetFailedAttempts(db, id);
            lastAuthenticatedUser = new User(id, name, email, hash, createdAt);
            return AuthResult.SUCCESS;
        } else {
            failedAttempts++;
            ContentValues cv = new ContentValues();
            cv.put("failedAttempts", failedAttempts);
            
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                cv.put("lockoutUntil", currentTime + LOCKOUT_DURATION_MS);
            }
            
            db.update(DBHelper.T_USER, cv, "id=?", new String[]{String.valueOf(id)});
            return failedAttempts >= MAX_FAILED_ATTEMPTS ? AuthResult.LOCKED : AuthResult.INVALID_CREDENTIALS;
        }
    }

    public User getByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.T_USER, null, "email=?", new String[]{email}, null, null, null);

        if (c != null && c.moveToFirst()) {
            User u = new User(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getString(c.getColumnIndexOrThrow("email")),
                    c.getString(c.getColumnIndexOrThrow("passwordHash")),
                    c.getLong(c.getColumnIndexOrThrow("createdAt"))
            );
            c.close();
            return u;
        }
        if (c != null) c.close();
        return null;
    }

    private void resetFailedAttempts(SQLiteDatabase db, int userId) {
        ContentValues cv = new ContentValues();
        cv.put("failedAttempts", 0);
        cv.put("lockoutUntil", 0);
        db.update(DBHelper.T_USER, cv, "id=?", new String[]{String.valueOf(userId)});
    }
}
