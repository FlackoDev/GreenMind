package com.example.greenmind.resource.learn;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenmind.data.db.dao.LearningContentDao;
import com.example.greenmind.databinding.ActivityLearningDetailBinding;
import com.example.greenmind.resource.model.LearningContent;

public class LearningDetailActivity extends AppCompatActivity {

    private ActivityLearningDetailBinding binding;
    private LearningContentDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLearningDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        dao = new LearningContentDao(this);

        int contentId = getIntent().getIntExtra("content_id", -1);
        if (contentId != -1) {
            loadContent(contentId);
        } else {
            finish();
        }
    }

    private void loadContent(int id) {
        LearningContent item = dao.getById(id);
        if (item != null) {
            binding.txtDetailTitle.setText(item.getTitle());
            binding.txtDetailCategory.setText(item.getCategory());
            binding.txtDetailTime.setText("Tempo di lettura: " + item.getReadingTimeMin() + " min");
            
            // Gestione dei caratteri a capo (\n) che arrivano dal database
            String content = item.getContent();
            if (content != null) {
                content = content.replace("\\n", "\n");
            }
            binding.txtDetailContent.setText(content);

        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
