package com.example.greenmind.resource.learn;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.greenmind.R;
import com.example.greenmind.databinding.ActivityLearnBinding;
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
    private LearningContentDao learningContentDao; // crea questo DAO come i tuoi altri

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLearnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigation();
        setupRecycler();

        learningContentDao = new LearningContentDao(this);
        loadContents();
    }

    private void setupRecycler() {
        adapter = new LearningContentAdapter(content -> {
            // quando clicchi "Leggi articolo completo >"gyk
//            Intent i = new Intent(this, LearningDetailActivity.class);
//            i.putExtra("content_id", content.getId());
//            startActivity(i);
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
