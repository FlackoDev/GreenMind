package com.example.greenmind.resource.profilo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.greenmind.R;
import com.example.greenmind.databinding.ActivityProfiloBinding;
import com.example.greenmind.resource.classifica.ClassificaActivity;
import com.example.greenmind.resource.home.HomeActivity;
import com.example.greenmind.resource.learn.LearnActivity;
import com.example.greenmind.resource.quiz.QuizActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfiloActivity extends AppCompatActivity {

    private ActivityProfiloBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfiloBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigation();
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
