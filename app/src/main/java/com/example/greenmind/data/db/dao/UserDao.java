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
            cv.put("role", "user");
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
                    userId = -1;
                }
            }
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    public enum AuthResult {
        SUCCESS,
        INVALID_CREDENTIALS,
        LOCKED,
        NEED_SET_PIN,
        NEED_VERIFY_PIN
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
        String role = c.getString(c.getColumnIndexOrThrow("role"));
        String pinHash = c.getString(c.getColumnIndexOrThrow("adminPinHash"));
        int failedAttempts = c.getInt(c.getColumnIndexOrThrow("failedAttempts"));
        long lockoutUntil = c.getLong(c.getColumnIndexOrThrow("lockoutUntil"));
        long createdAt = c.getLong(c.getColumnIndexOrThrow("createdAt"));
        c.close();

        long currentTime = System.currentTimeMillis();

        if (lockoutUntil > currentTime) {
            return AuthResult.LOCKED;
        }

        if (BCrypt.checkpw(password, hash)) {
            lastAuthenticatedUser = new User(id, name, email, hash, role, pinHash, createdAt);
            if (lastAuthenticatedUser.isAdmin()) {
                if (pinHash == null || pinHash.isEmpty()) {
                    return AuthResult.NEED_SET_PIN;
                } else {
                    return AuthResult.NEED_VERIFY_PIN;
                }
            }
            resetFailedAttempts(db, id);
            return AuthResult.SUCCESS;
        } else {
            handleFailedAttempt(db, id, failedAttempts);
            return isLocked(db, id) ? AuthResult.LOCKED : AuthResult.INVALID_CREDENTIALS;
        }
    }

    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        ContentValues cv = new ContentValues();
        cv.put("passwordHash", newHash);
        cv.put("failedAttempts", 0);
        cv.put("lockoutUntil", 0);
        int rows = db.update(DBHelper.T_USER, cv, "email=?", new String[]{email});
        return rows > 0;
    }

    public boolean verifyAdminPin(String email, String pin) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        User user = getByEmail(email);
        if (user == null || user.getAdminPinHash() == null) return false;
        if (BCrypt.checkpw(pin, user.getAdminPinHash())) {
            resetFailedAttempts(db, user.getId());
            return true;
        } else {
            handleFailedAttempt(db, user.getId(), getFailedAttempts(db, user.getId()));
            return false;
        }
    }

    private int getFailedAttempts(SQLiteDatabase db, int userId) {
        Cursor c = db.query(DBHelper.T_USER, new String[]{"failedAttempts"}, "id=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            int attempts = c.getInt(0);
            c.close();
            return attempts;
        }
        if (c != null) c.close();
        return 0;
    }

    public boolean setupAdminAccount(String email, String newPassword, String pin) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String passwordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String pinHash = BCrypt.hashpw(pin, BCrypt.gensalt());
        ContentValues cv = new ContentValues();
        cv.put("passwordHash", passwordHash);
        cv.put("adminPinHash", pinHash);
        int rows = db.update(DBHelper.T_USER, cv, "email=?", new String[]{email});
        return rows > 0;
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
                    c.getString(c.getColumnIndexOrThrow("role")),
                    c.getString(c.getColumnIndexOrThrow("adminPinHash")),
                    c.getLong(c.getColumnIndexOrThrow("createdAt"))
            );
            c.close();
            return u;
        }
        if (c != null) c.close();
        return null;
    }

    public boolean userExists(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.T_USER, new String[]{"id"}, "id=?", new String[]{String.valueOf(userId)}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    private void handleFailedAttempt(SQLiteDatabase db, int userId, int currentAttempts) {
        int nextAttempts = currentAttempts + 1;
        ContentValues cv = new ContentValues();
        cv.put("failedAttempts", nextAttempts);
        if (nextAttempts >= MAX_FAILED_ATTEMPTS) {
            cv.put("lockoutUntil", System.currentTimeMillis() + LOCKOUT_DURATION_MS);
        }
        db.update(DBHelper.T_USER, cv, "id=?", new String[]{String.valueOf(userId)});
    }

    private boolean isLocked(SQLiteDatabase db, int userId) {
        Cursor c = db.query(DBHelper.T_USER, new String[]{"lockoutUntil"}, "id=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            long lockout = c.getLong(0);
            c.close();
            return System.currentTimeMillis() < lockout;
        }
        if (c != null) c.close();
        return false;
    }

    private void resetFailedAttempts(SQLiteDatabase db, int userId) {
        ContentValues cv = new ContentValues();
        cv.put("failedAttempts", 0);
        cv.put("lockoutUntil", 0);
        db.update(DBHelper.T_USER, cv, "id=?", new String[]{String.valueOf(userId)});
    }
}
