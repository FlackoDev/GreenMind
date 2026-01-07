package com.example.greenmind.resource.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenmind.R;
import com.example.greenmind.data.auth.SessionManager;
import com.example.greenmind.data.repository.QuizManager;
import com.example.greenmind.databinding.ActivityHomeBinding;
import com.example.greenmind.resource.classifica.ClassificaActivity;
import com.example.greenmind.resource.learn.LearnActivity;
import com.example.greenmind.resource.model.Quiz;
import com.example.greenmind.resource.profilo.ProfiloActivity;
import com.example.greenmind.resource.quiz.QuizActivity;
import com.example.greenmind.resource.quiz.QuizPlayActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private SessionManager sessionManager;
    private QuizManager quizManager;
    private Quiz dailyQuiz;
    private boolean isCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        quizManager = new QuizManager(this);

        updateUI();
        setupBottomNavigation();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ricarica la sfida ogni volta che torni nella Home per aggiornare il tasto
        loadDailyChallenge();
    }

    private void updateUI() {
        String fullName = sessionManager.getUserName();
        binding.textWelcome.setText("Ciao, " + fullName + "!");
        binding.profileInitials.setText(getInitials(fullName));
    }

    private void loadDailyChallenge() {
        // Ora prendiamo il quiz del giorno con più punti
        dailyQuiz = quizManager.getFeaturedDailyQuiz();
        
        if (dailyQuiz != null) {
            binding.textChallengeName.setText(dailyQuiz.getTitle());
            String info = dailyQuiz.getNumQuestions() + " domande - " + dailyQuiz.getPoints() + " punti";
            binding.textChallengeDescription.setText(info);

            // Controlla se è già stato fatto
            isCompleted = quizManager.isQuizCompleted(sessionManager.getUserId(), dailyQuiz.getId());
            
            if (isCompleted) {
                binding.buttonAcceptChallenge.setText("Vedi Risultati");
            } else {
                binding.buttonAcceptChallenge.setText("Accetta Sfida");
            }
        }
    }

    private void setupClickListeners() {
        binding.profileInitials.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfiloActivity.class));
            overridePendingTransition(0, 0);
        });

        binding.cardQuickQuiz.setOnClickListener(v -> {
            openQuiz();
        });

        binding.buttonAcceptChallenge.setOnClickListener(v -> {
            openQuiz();
        });

        binding.cardReadArticle.setOnClickListener(v -> {
            startActivity(new Intent(this, LearnActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private void openQuiz() {
        if (dailyQuiz == null) return;
        
        // Corretto: carichiamo QuizPlayActivity per giocare il quiz del giorno
        Intent intent = new Intent(this, QuizPlayActivity.class);
        intent.putExtra("quiz_id", dailyQuiz.getId());
        intent.putExtra("is_view_only", isCompleted);
        startActivity(intent);
        overridePendingTransition(0, 0);
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
