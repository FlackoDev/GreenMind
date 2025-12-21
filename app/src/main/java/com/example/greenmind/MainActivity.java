package com.example.greenmind;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carichiamo il layout che contiene il NavHostFragment
        // Il NavHostFragment gestirà l'avvio partendo dallo SplashFragment
        setContentView(R.layout.activity_main);
    }
}
