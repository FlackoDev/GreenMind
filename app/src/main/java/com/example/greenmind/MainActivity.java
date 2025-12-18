package com.example.greenmind;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. INSTALLIAMO LA SPLASH SCREEN
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // Rimuoviamo il caricamento del binding e della toolbar
        // Poiché activity_main.xml ora contiene solo il NavHostFragment
        setContentView(R.layout.activity_main);

    }
}
