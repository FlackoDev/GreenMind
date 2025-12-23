package com.example.greenmind.resource.learn;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.greenmind.R;
import com.example.greenmind.databinding.ActivityLearnBinding;
import com.example.greenmind.resource.chat.ChatActivity;
import com.example.greenmind.resource.classifica.ClassificaActivity;
import com.example.greenmind.resource.home.HomeActivity;
import com.example.greenmind.resource.model.LearningContent;
import com.example.greenmind.resource.profilo.ProfiloActivity;
import com.example.greenmind.resource.quiz.QuizActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.greenmind.data.db.dao.LearningContentDao;

import java.util.List;

public class LearnActivity extends AppCompatActivity {

    private ActivityLearnBinding binding;
    private LearningContentAdapter adapter;
    private LearningContentDao learningContentDao;
    private boolean isSpeedDialVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLearnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigation();
        setupRecycler();
        setupSpeedDial();

        learningContentDao = new LearningContentDao(this);
    }

    private void setupSpeedDial() {
        binding.btnMainFab.setOnClickListener(v -> toggleSpeedDial());

        binding.fabAddAction.setOnClickListener(v -> {
            toggleSpeedDial();
            Intent intent = new Intent(this, AddLearningContentActivity.class);
            startActivity(intent);
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
        loadContents();
    }

    private void setupRecycler() {
        adapter = new LearningContentAdapter(content -> {
            Intent i = new Intent(this, LearningDetailActivity.class);
            i.putExtra("content_id", content.getId());
            startActivity(i);
        });

        binding.recyclerLearning.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerLearning.setAdapter(adapter);
    }

    private void loadContents() {
        List<LearningContent> list = learningContentDao.getAll();
        adapter.submitList(list);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.navigation_learn);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.navigation_learn) {
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
}
