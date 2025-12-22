package com.example.greenmind.resource.quiz;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.databinding.ActivityAddQuizBinding;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class AddQuizActivity extends AppCompatActivity {

    private ActivityAddQuizBinding binding;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DBHelper(this);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSaveQuiz.setOnClickListener(v -> saveNewQuiz());
    }

    private void saveNewQuiz() {
        String title = binding.inputQuizTitle.getText().toString().trim();
        String category = binding.inputQuizCategory.getText().toString().trim();
        String pointsStr = binding.inputQuizPoints.getText().toString().trim();
        String questionText = binding.inputQuestionText.getText().toString().trim();
        String correctAnswer = binding.inputCorrectAnswer.getText().toString().trim();
        String wrongAnswer = binding.inputWrongAnswer.getText().toString().trim();
        String explanation = binding.inputExplanation.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty() || pointsStr.isEmpty() || questionText.isEmpty() || correctAnswer.isEmpty() || wrongAnswer.isEmpty()) {
            Toast.makeText(this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // 1. Inserisci il Quiz
            ContentValues quizValues = new ContentValues();
            quizValues.put("title", title);
            quizValues.put("category", category);
            quizValues.put("difficulty", "Personalizzato");
            quizValues.put("points", Integer.parseInt(pointsStr));
            quizValues.put("numQuestions", 1);
            long quizId = db.insert(DBHelper.T_QUIZ, null, quizValues);

            if (quizId != -1) {
                // 2. Inserisci la Domanda
                ContentValues questionValues = new ContentValues();
                questionValues.put("quizId", quizId);
                questionValues.put("text", questionText);
                questionValues.put("explanation", explanation);
                long questionId = db.insert(DBHelper.T_QUESTION, null, questionValues);

                if (questionId != -1) {
                    // 3. Inserisci Opzione Corretta
                    ContentValues correctVal = new ContentValues();
                    correctVal.put("questionId", questionId);
                    correctVal.put("text", correctAnswer);
                    correctVal.put("isCorrect", 1);
                    db.insert(DBHelper.T_ANSWER_OPTION, null, correctVal);

                    // 4. Inserisci Opzione Sbagliata
                    ContentValues wrongVal = new ContentValues();
                    wrongVal.put("questionId", questionId);
                    wrongVal.put("text", wrongAnswer);
                    wrongVal.put("isCorrect", 0);
                    db.insert(DBHelper.T_ANSWER_OPTION, null, wrongVal);

                    db.setTransactionSuccessful();
                    Toast.makeText(this, "Nuovo Quiz creato con successo!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Errore durante il salvataggio", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
        }
    }
}
