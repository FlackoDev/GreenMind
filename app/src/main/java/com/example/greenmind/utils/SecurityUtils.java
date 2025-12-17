package com.example.greenmind.utils;

import android.util.Patterns;
import java.util.regex.Pattern;

public class SecurityUtils {

    // Regex per la password: Almeno 8 caratteri, una maiuscola, un numero e un carattere speciale
    private static final String PASSWORD_PATTERN = 
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    /**
     * Verifica se l'email è nel formato corretto.
     */
    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Verifica se la password rispetta i criteri di sicurezza.
     */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        return pattern.matcher(password).matches();
    }
}
