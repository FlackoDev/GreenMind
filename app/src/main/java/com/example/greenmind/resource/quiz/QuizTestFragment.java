package com.example.greenmind.resource.quiz;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.greenmind.R;
import com.example.greenmind.data.db.dao.QuizDao;
import com.example.greenmind.model.Quiz;

import java.util.List;

public class QuizTestFragment extends Fragment {

    private EditText etId, etTitle, etCategory, etDifficulty;
    private TextView tvResult;

    private QuizDao quizDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quiz_test, container, false);

        etId = v.findViewById(R.id.etQuizId);
        etTitle = v.findViewById(R.id.etQuizTitle);
        etCategory = v.findViewById(R.id.etQuizCategory);
        etDifficulty = v.findViewById(R.id.etQuizDifficulty);
        tvResult = v.findViewById(R.id.tvResult);

        Button btnInsertSample = v.findViewById(R.id.btnInsertSample);
        Button btnSave = v.findViewById(R.id.btnSave);
        Button btnLoad = v.findViewById(R.id.btnLoad);

        quizDao = new QuizDao(requireContext());

        btnInsertSample.setOnClickListener(view -> insertSampleData());
        btnSave.setOnClickListener(view -> saveFromInputs());
        btnLoad.setOnClickListener(view -> loadAll());

        return v;
    }

    private void insertSampleData() {
        quizDao.upsert(new Quiz(1, "Quiz Clima", "Emergenze climatiche", "Facile"));
        quizDao.upsert(new Quiz(2, "Quiz Rifiuti", "Gestione rifiuti", "Medio"));
        quizDao.upsert(new Quiz(3, "Quiz Energia", "Sostenibilità", "Difficile"));

        Toast.makeText(requireContext(), "Quiz di esempio inseriti!", Toast.LENGTH_SHORT).show();
        loadAll();
    }

    private void saveFromInputs() {
        String idStr = etId.getText().toString().trim();
        String title = etTitle.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String difficulty = etDifficulty.getText().toString().trim();

        if (TextUtils.isEmpty(idStr) || TextUtils.isEmpty(title)) {
            Toast.makeText(requireContext(), "ID e Titolo sono obbligatori", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(idStr);

        Quiz q = new Quiz(id, title, category, difficulty);
        quizDao.upsert(q);

        Toast.makeText(requireContext(), "Salvato (upsert)!", Toast.LENGTH_SHORT).show();
        loadAll();
    }

    private void loadAll() {
        List<Quiz> quizzes = quizDao.getAll();

        if (quizzes.isEmpty()) {
            tvResult.setText("Nessun quiz nel DB.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Quiz q : quizzes) {
            sb.append("ID: ").append(q.getId())
                    .append(" | ").append(q.getTitle())
                    .append(" | Cat: ").append(q.getCategory())
                    .append(" | Diff: ").append(q.getDifficulty())
                    .append("\n");
        }

        tvResult.setText(sb.toString());
    }
}
