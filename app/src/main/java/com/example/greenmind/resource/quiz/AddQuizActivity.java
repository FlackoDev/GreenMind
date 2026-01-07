package com.example.greenmind.resource.quiz;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.greenmind.R;
import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.databinding.ActivityAddQuizBinding;

import java.util.ArrayList;
import java.util.List;

public class AddQuizActivity extends AppCompatActivity {

    private ActivityAddQuizBinding binding;
    private DBHelper dbHelper;
    private List<View> answerViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);

        setupDifficultySpinner();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAddAnswer.setOnClickListener(v -> addAnswerField("", false));
        binding.btnSaveQuiz.setOnClickListener(v -> saveNewQuiz());

        addAnswerField("", true);
        addAnswerField("", false);
    }

    private void setupDifficultySpinner() {
        String[] difficulties = new String[]{"Facile", "Medio", "Difficile"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, difficulties);
        binding.spinnerDifficulty.setAdapter(adapter);
        binding.spinnerDifficulty.setText(difficulties[0], false);
    }

    private void addAnswerField(String text, boolean isCorrect) {
        if (answerViews.size() >= 8) {
            Toast.makeText(this, "Massimo 8 risposte permesse", Toast.LENGTH_SHORT).show();
            return;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.item_admin_answer, binding.layoutAnswersContainer, false);
        EditText input = view.findViewById(R.id.input_answer_text);
        CheckBox check = view.findViewById(R.id.check_is_correct);
        View btnDelete = view.findViewById(R.id.btn_delete_answer);

        input.setText(text);
        check.setChecked(isCorrect);

        btnDelete.setOnClickListener(v -> {
            binding.layoutAnswersContainer.removeView(view);
            answerViews.remove(view);
        });

        binding.layoutAnswersContainer.addView(view);
        answerViews.add(view);
    }

    private void saveNewQuiz() {
        String title = binding.inputQuizTitle.getText().toString().trim();
        String category = binding.inputQuizCategory.getText().toString().trim();
        String difficulty = binding.spinnerDifficulty.getText().toString();
        String pointsStr = binding.inputQuizPoints.getText().toString().trim();
        String questionText = binding.inputQuestionText.getText().toString().trim();
        String explanation = binding.inputExplanation.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty() || pointsStr.isEmpty() || questionText.isEmpty()) {
            Toast.makeText(this, "Compila i dati principali!", Toast.LENGTH_SHORT).show();
            return;
        }

        int correctCount = 0;
        List<AnswerData> answers = new ArrayList<>();
        for (View v : answerViews) {
            EditText input = v.findViewById(R.id.input_answer_text);
            CheckBox check = v.findViewById(R.id.check_is_correct);
            String txt = input.getText().toString().trim();
            if (!txt.isEmpty()) {
                boolean isCorr = check.isChecked();
                if (isCorr) correctCount++;
                answers.add(new AnswerData(txt, isCorr));
            }
        }

        if (answers.size() < 2) {
            Toast.makeText(this, "Inserisci almeno 2 risposte!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (correctCount == 0 || correctCount > 2) {
            Toast.makeText(this, "Seleziona da 1 a 2 risposte corrette!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues qv = new ContentValues();
            qv.put("title", title);
            qv.put("category", category);
            qv.put("difficulty", difficulty);
            qv.put("points", Integer.parseInt(pointsStr));
            qv.put("numQuestions", 1);
            long qId = db.insert(DBHelper.T_QUIZ, null, qv);

            ContentValues questV = new ContentValues();
            questV.put("quizId", qId);
            questV.put("text", questionText);
            questV.put("explanation", explanation);
            long questId = db.insert(DBHelper.T_QUESTION, null, questV);

            for (AnswerData ad : answers) {
                ContentValues av = new ContentValues();
                av.put("questionId", questId);
                av.put("text", ad.text);
                av.put("isCorrect", ad.isCorrect ? 1 : 0);
                db.insert(DBHelper.T_ANSWER_OPTION, null, av);
            }

            db.setTransactionSuccessful();
            Toast.makeText(this, "Quiz pubblicato con successo!", Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
        }
    }

    private static class AnswerData {
        String text;
        boolean isCorrect;
        AnswerData(String t, boolean c) { text = t; isCorrect = c; }
    }
}
