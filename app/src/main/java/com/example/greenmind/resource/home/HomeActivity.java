package com.example.greenmind.resource.home;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenmind.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity { // <-- CORRETTO: Estende AppCompatActivity

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Usa ViewBinding per caricare il layout, come nel resto del progetto
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
