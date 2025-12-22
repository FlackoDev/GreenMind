package com.example.greenmind.resource.profilo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.greenmind.MainActivity;
import com.example.greenmind.R;
import com.example.greenmind.data.auth.SessionManager;
import com.example.greenmind.data.db.dao.BadgeDao;
import com.example.greenmind.data.db.dao.QuizResultDao;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfiloActivity extends AppCompatActivity {

    private ActivityProfiloBinding binding;
    private SessionManager sessionManager;
    private UserDao userDao;
    private UserStatsDao userStatsDao;
    private QuizResultDao quizResultDao;
    private BadgeDao badgeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfiloBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        userDao = new UserDao(this);
        userStatsDao = new UserStatsDao(this);
        quizResultDao = new QuizResultDao(this);
        badgeDao = new BadgeDao(this);

        setupBottomNavigation();
        setupOverlayButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        int currentPoints = 0;
        if (stats != null) {
            currentPoints = stats.getTotalPoints();
            binding.textTotalPoints.setText(String.valueOf(currentPoints));
            binding.textQuizzesDone.setText(String.valueOf(stats.getTotalQuizzes()));
            
            String change = (stats.getWeeklyChangePerc() >= 0 ? "+" : "") + (int)stats.getWeeklyChangePerc() + "%";
            binding.textWeeklyChange.setText(change);
            
            int level = (currentPoints / 500) + 1;
            binding.textLevelBadge.setText("LV. " + level);
        }

        setupBadges(currentPoints);
        setupWeeklyChart(userId);
    }

    private void setupWeeklyChart(int userId) {
        Map<Integer, Integer> pointsMap = quizResultDao.getPointsLast7Days(userId);
        
        int maxPoints = 0;
        for (int points : pointsMap.values()) {
            if (points > maxPoints) maxPoints = points;
        }

        int chartMax = Math.max(maxPoints, 100); 

        updateBarHeight(binding.barMon, pointsMap.getOrDefault(Calendar.MONDAY, 0), chartMax);
        updateBarHeight(binding.barTue, pointsMap.getOrDefault(Calendar.TUESDAY, 0), chartMax);
        updateBarHeight(binding.barWed, pointsMap.getOrDefault(Calendar.WEDNESDAY, 0), chartMax);
        updateBarHeight(binding.barThu, pointsMap.getOrDefault(Calendar.THURSDAY, 0), chartMax);
        updateBarHeight(binding.barFri, pointsMap.getOrDefault(Calendar.FRIDAY, 0), chartMax);
        updateBarHeight(binding.barSat, pointsMap.getOrDefault(Calendar.SATURDAY, 0), chartMax);
        updateBarHeight(binding.barSun, pointsMap.getOrDefault(Calendar.SUNDAY, 0), chartMax);

        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        highlightToday(today);
    }

    private void updateBarHeight(View bar, int points, int max) {
        bar.post(() -> {
            int containerHeight = binding.chartContainer.getHeight();
            if (containerHeight == 0) containerHeight = (int) (100 * getResources().getDisplayMetrics().density);
            
            int minHeight = (int) (10 * getResources().getDisplayMetrics().density);
            int calculatedHeight = (int) (((float) points / max) * containerHeight);
            
            ViewGroup.LayoutParams params = bar.getLayoutParams();
            params.height = Math.max(calculatedHeight, minHeight);
            bar.setLayoutParams(params);
        });
    }

    private void highlightToday(int dayOfWeek) {
        binding.barMon.setBackgroundResource(R.drawable.weekly_bar_background_light);
        binding.barTue.setBackgroundResource(R.drawable.weekly_bar_background_light);
        binding.barWed.setBackgroundResource(R.drawable.weekly_bar_background_light);
        binding.barThu.setBackgroundResource(R.drawable.weekly_bar_background_light);
        binding.barFri.setBackgroundResource(R.drawable.weekly_bar_background_light);
        binding.barSat.setBackgroundResource(R.drawable.weekly_bar_background_light);
        binding.barSun.setBackgroundResource(R.drawable.weekly_bar_background_light);

        View todayBar = null;
        switch (dayOfWeek) {
            case Calendar.MONDAY: todayBar = binding.barMon; break;
            case Calendar.TUESDAY: todayBar = binding.barTue; break;
            case Calendar.WEDNESDAY: todayBar = binding.barWed; break;
            case Calendar.THURSDAY: todayBar = binding.barThu; break;
            case Calendar.FRIDAY: todayBar = binding.barFri; break;
            case Calendar.SATURDAY: todayBar = binding.barSat; break;
            case Calendar.SUNDAY: todayBar = binding.barSun; break;
        }
        if (todayBar != null) {
            todayBar.setBackgroundResource(R.drawable.weekly_bar_background_primary);
        }
    }

    private void setupBadges(int userPoints) {
        List<Badge> allBadges = badgeDao.getAllBadges();
        String highestBadgeName = "ECO BEGINNER";
        
        for (Badge b : allBadges) {
            if (userPoints >= b.getRequiredPoints()) {
                b.setUnlocked(true);
                highestBadgeName = b.getName().replace("\n", " ");
            } else {
                b.setUnlocked(false);
            }
        }
        
        binding.textRank.setText(highestBadgeName);

        // Aggiungiamo il tasto speciale alla fine
        allBadges.add(new Badge(99, "VEDI\nTUTTI", true));

        BadgeAdapter adapter = new BadgeAdapter(allBadges);
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
