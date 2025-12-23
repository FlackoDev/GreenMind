package com.example.greenmind.resource.quiz;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.greenmind.R;
import com.example.greenmind.data.auth.SessionManager;
import com.example.greenmind.data.db.dao.QuizDao;
import com.example.greenmind.data.repository.QuizManager;
import com.example.greenmind.databinding.ActivityQuizBinding;
import com.example.greenmind.resource.chat.ChatActivity;
import com.example.greenmind.resource.classifica.ClassificaActivity;
import com.example.greenmind.resource.home.HomeActivity;
import com.example.greenmind.resource.learn.LearnActivity;
import com.example.greenmind.resource.model.Quiz;
import com.example.greenmind.resource.profilo.ProfiloActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private QuizDao quizDao;
    private QuizManager quizManager;
    private SessionManager sessionManager;
    private boolean isSpeedDialVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        quizDao = new QuizDao(this);
        quizManager = new QuizManager(this);
        sessionManager = new SessionManager(this);

        setupBottomNavigation();
        setupSpeedDial();
    }

    private void setupSpeedDial() {
        binding.btnMainFab.setOnClickListener(v -> toggleSpeedDial());

        binding.fabAddAction.setOnClickListener(v -> {
            toggleSpeedDial();
            startActivity(new Intent(this, AddQuizActivity.class));
        });

        binding.fabGeminiAction.setOnClickListener(v -> {
            toggleSpeedDial();
            ChatActivity chatSheet = new ChatActivity();
            chatSheet.show(getSupportFragmentManager(), "ChatBottomSheet");
        });
    }

    private void toggleSpeedDial() {
        if (!isSpeedDialVisible) {
            binding.fabAddAction.show();
            binding.fabGeminiAction.show();
            binding.btnMainFab.setRotation(45f);
            isSpeedDialVisible = true;
        } else {
            binding.fabAddAction.hide();
            binding.fabGeminiAction.hide();
            binding.btnMainFab.setRotation(0f);
            isSpeedDialVisible = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        List<Quiz> allQuizzes = quizDao.getAll();
        
        QuizAdapter adapter = new QuizAdapter(allQuizzes, quizManager, sessionManager.getUserId(), (quiz, isCompleted) -> {
            Intent intent = new Intent(QuizActivity.this, QuizPlayActivity.class);
            intent.putExtra("quiz_id", quiz.getId());
            intent.putExtra("is_view_only", isCompleted);
            startActivity(intent);
        });

        binding.recyclerQuizList.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerQuizList.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.navigation_quiz);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.navigation_learn) {
                startActivity(new Intent(this, LearnActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.navigation_quiz) {
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
}
