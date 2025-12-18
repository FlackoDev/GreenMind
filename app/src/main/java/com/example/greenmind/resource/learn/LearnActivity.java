package com.example.greenmind.resource.learn;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.greenmind.R;
import com.example.greenmind.databinding.ActivityLearnBinding;
import com.example.greenmind.resource.classifica.ClassificaActivity;
import com.example.greenmind.resource.home.HomeActivity;
import com.example.greenmind.resource.profilo.ProfiloActivity;
import com.example.greenmind.resource.quiz.QuizActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LearnActivity extends AppCompatActivity {

    private ActivityLearnBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLearnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigation();
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
