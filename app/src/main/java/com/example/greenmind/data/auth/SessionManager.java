package com.example.greenmind.data.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SessionManager {

    private static final String PREF_NAME = "secure_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    // Sicurezza Globale
    private static final String KEY_GLOBAL_FAILED_ATTEMPTS = "global_failed_attempts";
    private static final String KEY_GLOBAL_LOCKOUT_UNTIL = "global_lockout_until";
    private static final int MAX_GLOBAL_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            sharedPreferences = EncryptedSharedPreferences.create(
                    PREF_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            editor = sharedPreferences.edit();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    public void createLoginSession(int userId, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        resetGlobalFailures();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "Utente");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    // --- Gestione Blocco Globale ---

    public boolean isGlobalLocked() {
        long lockoutUntil = sharedPreferences.getLong(KEY_GLOBAL_LOCKOUT_UNTIL, 0);
        return System.currentTimeMillis() < lockoutUntil;
    }

    public void incrementGlobalFailures() {
        int current = sharedPreferences.getInt(KEY_GLOBAL_FAILED_ATTEMPTS, 0) + 1;
        editor.putInt(KEY_GLOBAL_FAILED_ATTEMPTS, current);
        
        if (current >= MAX_GLOBAL_ATTEMPTS) {
            editor.putLong(KEY_GLOBAL_LOCKOUT_UNTIL, System.currentTimeMillis() + LOCKOUT_DURATION_MS);
        }
        editor.apply();
    }

    public void resetGlobalFailures() {
        editor.putInt(KEY_GLOBAL_FAILED_ATTEMPTS, 0);
        editor.putLong(KEY_GLOBAL_LOCKOUT_UNTIL, 0);
        editor.apply();
    }
}
