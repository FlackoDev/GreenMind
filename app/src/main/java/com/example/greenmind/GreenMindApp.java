package com.example.greenmind;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.greenmind.data.auth.SessionManager;

public class GreenMindApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
