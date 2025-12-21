package com.example.greenmind.resource.profilo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.greenmind.MainActivity;
import com.example.greenmind.R;
import com.example.greenmind.data.auth.SessionManager;
import com.example.greenmind.data.db.dao.UserDao;
import com.example.greenmind.data.db.dao.UserStatsDao;
import com.example.greenmind.databinding.ActivityProfiloBinding;
import com.example.greenmind.resource.classifica.ClassificaActivity;
import com.example.greenmind.resource.home.HomeActivity;
import com.example.greenmind.resource.learn.LearnActivity;
import com.example.greenmind.resource.model.Badge;
import com.example.greenmind.resource.model.User;
import com.example.greenmind.resource.model.UserStats;
import com.example.greenmind.resource.quiz.QuizActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfiloActivity extends AppCompatActivity {

    private ActivityProfiloBinding binding;
    private SessionManager sessionManager;
    private UserDao userDao;
    private UserStatsDao userStatsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfiloBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        userDao = new UserDao(this);
        userStatsDao = new UserStatsDao(this);

        setupBottomNavigation();
        setupOverlayButtons();
        setupBadges();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ricarica i dati ogni volta che la schermata viene visualizzata
        loadUserData();
    }

    private void setupOverlayButtons() {
        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        binding.btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });
    }

    private void loadUserData() {
        int userId = sessionManager.getUserId();
        String email = sessionManager.getUserEmail();
        
        User user = userDao.getByEmail(email);
        UserStats stats = userStatsDao.getStatsByUserId(userId);

        if (user != null) {
            binding.textFullName.setText(user.getName());
            binding.textInitials.setText(getInitials(user.getName()));
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.ITALIAN);
            String dateStr = sdf.format(new Date(user.getCreatedAt()));
            binding.textMemberSince.setText("Membro dal " + dateStr);
        }

        if (stats != null) {
            binding.textTotalPoints.setText(String.valueOf(stats.getTotalPoints()));
            binding.textQuizzesDone.setText(String.valueOf(stats.getTotalQuizzes()));
            
            String change = (stats.getWeeklyChangePerc() >= 0 ? "+" : "") + (int)stats.getWeeklyChangePerc() + "%";
            binding.textWeeklyChange.setText(change);
            
            int level = (stats.getTotalPoints() / 500) + 1;
            binding.textLevelBadge.setText("LV. " + level);
            
            String rank = "ECO BEGINNER";
            if (level > 2) rank = "ECO WARRIOR";
            if (level > 5) rank = "RECYCLE MASTER";
            if (level > 10) rank = "CLIMATE GUARDIAN";
            binding.textRank.setText(rank);
        }
    }

    private void setupBadges() {
        List<Badge> badges = new ArrayList<>();
        badges.add(new Badge(1, "ECO\nWARRIOR", "", 0));
        badges.add(new Badge(2, "RECYCLE\nMASTER", "", 0));
        badges.add(new Badge(3, "CLIMATE\nGUARDIAN", "", 0));
        
        badges.add(new Badge(99, "VEDI\nTUTTI", true));

        BadgeAdapter adapter = new BadgeAdapter(badges);
        binding.recyclerBadges.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerBadges.setAdapter(adapter);
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "??";
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(parts.length, 2); i++) {
            if (!parts[i].isEmpty()) initials.append(parts[i].charAt(0));
        }
        return initials.toString().toUpperCase();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.navigation_profilo);

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
                startActivity(new Intent(this, ClassificaActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.navigation_profilo) {
                return true;
            }
            return false;
        });
    }
}
