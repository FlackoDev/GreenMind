package com.example.greenmind.resource.home;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenmind.R;
import com.example.greenmind.data.auth.SessionManager;
import com.example.greenmind.databinding.ActivityHomeBinding;
import com.example.greenmind.resource.ai.ChatBottomSheetFragment;
import com.example.greenmind.resource.classifica.ClassificaActivity;
import com.example.greenmind.resource.learn.LearnActivity;
import com.example.greenmind.resource.profilo.ProfiloActivity;
import com.example.greenmind.resource.quiz.QuizActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        updateUI();
        setupBottomNavigation();
        setupClickListeners();
    }

    private void updateUI() {
        String fullName = sessionManager.getUserName();
        
        // Imposta il saluto dinamico
        binding.textWelcome.setText("Ciao, " + fullName + "!");

        // Calcola e imposta le iniziali
        binding.profileInitials.setText(getInitials(fullName));
    }

    private void setupClickListeners() {
        // 1. AZ -> Profilo
        binding.profileInitials.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfiloActivity.class));
            overridePendingTransition(0, 0);
        });

        // 2. Quiz Rapido -> Quiz
        binding.cardQuickQuiz.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizActivity.class));
            overridePendingTransition(0, 0);
        });

        // 3. Accetta Sfida -> Quiz
        binding.buttonAcceptChallenge.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizActivity.class));
            overridePendingTransition(0, 0);
        });

        // 4. Leggi Articolo -> Learn (Impara)
        binding.cardReadArticle.setOnClickListener(v -> {
            startActivity(new Intent(this, LearnActivity.class));
            overridePendingTransition(0, 0);
        });

        // 5. Chat AI -> Apre il BottomSheet
        binding.fabAiChat.setOnClickListener(v -> {
            ChatBottomSheetFragment chatSheet = new ChatBottomSheetFragment();
            chatSheet.show(getSupportFragmentManager(), chatSheet.getTag());
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                return true;
            } else if (id == R.id.navigation_learn) {
                startActivity(new Intent(this, LearnActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.navigation_quiz) {
                startActivity(new Intent(this, QuizActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.navigation_classifica) {
                startActivity(new Intent(this, ClassificaActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.navigation_profilo) {
                startActivity(new Intent(this, ProfiloActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "??";
        
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        
        for (int i = 0; i < Math.min(parts.length, 2); i++) {
            if (!parts[i].isEmpty()) {
                initials.append(parts[i].charAt(0));
            }
        }
        
        return initials.toString().toUpperCase();
    }
}
