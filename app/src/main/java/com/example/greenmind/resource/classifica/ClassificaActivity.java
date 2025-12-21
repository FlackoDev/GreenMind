package com.example.greenmind.resource.classifica;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.greenmind.R;
import com.example.greenmind.data.auth.SessionManager;
import com.example.greenmind.data.db.dao.LeaderboardEntryDao;
import com.example.greenmind.databinding.ActivityClassificaBinding;
import com.example.greenmind.resource.home.HomeActivity;
import com.example.greenmind.resource.learn.LearnActivity;
import com.example.greenmind.resource.model.LeaderboardEntry;
import com.example.greenmind.resource.profilo.ProfiloActivity;
import com.example.greenmind.resource.quiz.QuizActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class ClassificaActivity extends AppCompatActivity {

    private ActivityClassificaBinding binding;
    private LeaderboardEntryDao leaderboardDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassificaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        leaderboardDao = new LeaderboardEntryDao(this);
        sessionManager = new SessionManager(this);

        setupBottomNavigation();
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        List<LeaderboardEntry> fullList = leaderboardDao.getGlobalLeaderboard();
        
        if (fullList.isEmpty()) {
            binding.podiumContainer.setVisibility(View.GONE);
            return;
        }

        // --- POPOLA PODIO ---
        // 1° Posto
        if (fullList.size() >= 1) {
            LeaderboardEntry first = fullList.get(0);
            binding.tvFirstInitials.setText(first.getInitials());
            binding.tvFirstName.setText(first.getUserName());
            binding.tvFirstPoints.setText(first.getPoints() + " pt");
        } else {
            binding.podiumFirst.setVisibility(View.INVISIBLE);
        }

        // 2° Posto
        if (fullList.size() >= 2) {
            LeaderboardEntry second = fullList.get(1);
            binding.tvSecondInitials.setText(second.getInitials());
            binding.tvSecondName.setText(second.getUserName());
            binding.tvSecondPoints.setText(second.getPoints() + " pt");
        } else {
            binding.podiumSecond.setVisibility(View.INVISIBLE);
        }

        // 3° Posto
        if (fullList.size() >= 3) {
            LeaderboardEntry third = fullList.get(2);
            binding.tvThirdInitials.setText(third.getInitials());
            binding.tvThirdName.setText(third.getUserName());
            binding.tvThirdPoints.setText(third.getPoints() + " pt");
        } else {
            binding.podiumThird.setVisibility(View.INVISIBLE);
        }

        // --- POPOLA LISTA (dal 4° in poi) ---
        List<LeaderboardEntry> remainingList = new ArrayList<>();
        if (fullList.size() > 3) {
            remainingList = fullList.subList(3, fullList.size());
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LeaderboardAdapter adapter = new LeaderboardAdapter(remainingList, sessionManager.getUserId());
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.navigation_classifica);

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
                startActivity(new Intent(this, QuizActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.navigation_classifica) {
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
