package com.example.greenmind.resource.quiz;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.greenmind.R;
import com.example.greenmind.data.auth.SessionManager;
import com.example.greenmind.data.db.DBHelper;
import com.example.greenmind.data.db.dao.AnswerOptionDao;
import com.example.greenmind.data.db.dao.QuestionDao;
import com.example.greenmind.data.db.dao.QuizDao;
import com.example.greenmind.databinding.ActivityQuizPlayBinding;
import com.example.greenmind.databinding.ItemAnswerOptionBinding;
import com.example.greenmind.resource.model.AnswerOption;
import com.example.greenmind.resource.model.Question;
import com.example.greenmind.resource.model.Quiz;

import java.util.List;

public class QuizPlayActivity extends AppCompatActivity {

    private ActivityQuizPlayBinding binding;
    private QuizDao quizDao;
    private QuestionDao questionDao;
    private AnswerOptionDao answerOptionDao;
    private SessionManager sessionManager;

    private Quiz currentQuiz;
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;
    private boolean isViewOnly = false;
    private boolean hasAnswered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        quizDao = new QuizDao(this);
        questionDao = new QuestionDao(this);
        answerOptionDao = new AnswerOptionDao(this);
        sessionManager = new SessionManager(this);

        int quizId = getIntent().getIntExtra("quiz_id", -1);
        isViewOnly = getIntent().getBooleanExtra("is_view_only", false);

        if (quizId == -1) {
            finish();
            return;
        }

        currentQuiz = quizDao.getById(quizId);
        questionList = questionDao.getByQuizId(quizId);

        if (currentQuiz == null || questionList == null || questionList.isEmpty()) {
            Toast.makeText(this, "Errore nel caricamento del quiz", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupUI();
        displayQuestion();
    }

    private void setupUI() {
        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonNext.setOnClickListener(v -> handleNextButtonClick());
        
        if (isViewOnly) {
            binding.buttonNext.setText("Avanti");
        }
    }

    private void displayQuestion() {
        hasAnswered = false;
        binding.buttonNext.setEnabled(isViewOnly);
        binding.cardExplanation.setVisibility(isViewOnly ? View.VISIBLE : View.GONE);
        binding.layoutOptions.removeAllViews();

        Question question = questionList.get(currentQuestionIndex);
        binding.textQuestion.setText(question.getText());
        binding.textExplanation.setText(question.getExplanation());

        int progress = (int) (((float) (currentQuestionIndex + 1) / questionList.size()) * 100);
        binding.quizProgressBar.setProgress(progress);
        binding.textProgressCount.setText((currentQuestionIndex + 1) + "/" + questionList.size());

        List<AnswerOption> options = answerOptionDao.getByQuestionId(question.getId());
        for (AnswerOption option : options) {
            addOptionView(option);
        }

        if (currentQuestionIndex == questionList.size() - 1) {
            binding.buttonNext.setText(isViewOnly ? "Chiudi" : "Termina");
        } else {
            binding.buttonNext.setText(isViewOnly ? "Avanti" : "Prossima Domanda");
        }
    }

    private void addOptionView(AnswerOption option) {
        ItemAnswerOptionBinding optionBinding = ItemAnswerOptionBinding.inflate(LayoutInflater.from(this), binding.layoutOptions, false);
        optionBinding.textOption.setText(option.getText());
        optionBinding.getRoot().setTag(option);

        if (isViewOnly) {
            if (option.isCorrect()) {
                styleOptionCorrect(optionBinding);
                optionBinding.imageStatus.setVisibility(View.VISIBLE);
                optionBinding.imageStatus.setImageResource(android.R.drawable.checkbox_on_background);
            }
            optionBinding.getRoot().setClickable(false);
        } else {
            optionBinding.getRoot().setOnClickListener(v -> {
                if (!hasAnswered) {
                    handleAnswerSelection(option, optionBinding);
                }
            });
        }

        binding.layoutOptions.addView(optionBinding.getRoot());
    }

    private void handleAnswerSelection(AnswerOption selectedOption, ItemAnswerOptionBinding selectedBinding) {
        hasAnswered = true;
        binding.buttonNext.setEnabled(true);
        binding.cardExplanation.setVisibility(View.VISIBLE);

        if (selectedOption.isCorrect()) {
            correctAnswersCount++;
            styleOptionCorrect(selectedBinding);
        } else {
            styleOptionWrong(selectedBinding);
            highlightCorrectAnswer();
        }
    }

    private void highlightCorrectAnswer() {
        for (int i = 0; i < binding.layoutOptions.getChildCount(); i++) {
            View v = binding.layoutOptions.getChildAt(i);
            AnswerOption opt = (AnswerOption) v.getTag();
            if (opt != null && opt.isCorrect()) {
                ItemAnswerOptionBinding b = ItemAnswerOptionBinding.bind(v);
                styleOptionCorrect(b);
            }
        }
    }

    private void styleOptionCorrect(ItemAnswerOptionBinding b) {
        b.cardOption.setStrokeColor(ContextCompat.getColor(this, R.color.correct_stroke));
        b.cardOption.setCardBackgroundColor(ContextCompat.getColor(this, R.color.correct_background));
        b.textOption.setTextColor(ContextCompat.getColor(this, R.color.correct_text));
    }

    private void styleOptionWrong(ItemAnswerOptionBinding b) {
        b.cardOption.setStrokeColor(ContextCompat.getColor(this, R.color.wrong_stroke));
        b.cardOption.setCardBackgroundColor(ContextCompat.getColor(this, R.color.wrong_background));
        b.textOption.setTextColor(ContextCompat.getColor(this, R.color.wrong_text));
    }

    private void handleNextButtonClick() {
        if (currentQuestionIndex < questionList.size() - 1) {
            currentQuestionIndex++;
            displayQuestion();
        } else {
            if (!isViewOnly) {
                saveResultsAndFinish();
            } else {
                finish();
            }
        }
    }

    private void saveResultsAndFinish() {
        int totalScore = (currentQuiz.getPoints() * correctAnswersCount) / questionList.size();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            finish();
            return;
        }
        
        long now = System.currentTimeMillis();

        db.beginTransaction();
        try {
            ContentValues cvResult = new ContentValues();
            cvResult.put("userId", userId);
            cvResult.put("quizId", currentQuiz.getId());
            cvResult.put("score", totalScore);
            cvResult.put("date", now);
            db.insert(DBHelper.T_QUIZ_RESULT, null, cvResult);

            db.execSQL("INSERT OR IGNORE INTO " + DBHelper.T_USER_STATS + 
                       " (userId, totalQuizzes, totalPoints, weeklyChangePerc) VALUES (" + userId + ", 0, 0, 0)");
            
            db.execSQL("UPDATE " + DBHelper.T_USER_STATS + 
                       " SET totalPoints = totalPoints + " + totalScore + 
                       ", totalQuizzes = totalQuizzes + 1 WHERE userId = " + userId);

            db.setTransactionSuccessful();
            Toast.makeText(this, "Quiz completato! Punti: " + totalScore, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            finish();
        }
    }
}
