package com.example.greenmind.resource.learn;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenmind.databinding.ActivityAddLearningContentBinding;
import com.example.greenmind.resource.model.LearningContent;
import com.example.greenmind.data.db.dao.LearningContentDao;

public class AddLearningContentActivity extends AppCompatActivity {

    private ActivityAddLearningContentBinding binding;
    private LearningContentDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLearningContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao = new LearningContentDao(this);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSave.setOnClickListener(v -> saveContent());
    }

    private void saveContent() {
        String title = binding.inputTitle.getText().toString().trim();
        String category = binding.inputCategory.getText().toString().trim();
        String preview = binding.inputPreview.getText().toString().trim();
        String contentText = binding.inputContent.getText().toString().trim();
        String timeStr = binding.inputTime.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty() || timeStr.isEmpty() || contentText.isEmpty()) {
            Toast.makeText(this, "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int time = Integer.parseInt(timeStr);

            LearningContent content = new LearningContent(
                    0,
                    title,
                    category,
                    time,
                    preview,
                    contentText
            );

            dao.insert(content);

            Toast.makeText(this, "Articolo aggiunto!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Inserisci un tempo di lettura valido", Toast.LENGTH_SHORT).show();
        }
    }
}
