package com.example.greenmind.resource.quiz;

import android.content.ContentValues;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizPlayActivity extends AppCompatActivity {

    private ActivityQuizPlayBinding binding;
    private QuizDao quizDao;
    private QuestionDao questionDao;
    private AnswerOptionDao answerOptionDao;
    private SessionManager sessionManager;

    private Quiz currentQuiz;
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private double totalPointsEarned = 0;
    private boolean isViewOnly = false;
    private boolean hasAnsweredCorrectlyAll = false;
    private boolean hasAnsweredWrong = false;

    // Per memorizzare le risposte date durante la sessione: QuestionId -> List of OptionId
    private Map<Integer, List<Integer>> sessionAnswers = new HashMap<>();
    // Per caricare le risposte salvate: QuestionId -> List of OptionId
    private Map<Integer, List<Integer>> savedAnswers = new HashMap<>();
    
    // Risposte corrette trovate nella domanda corrente
    private List<Integer> currentQuestionCorrectFound = new ArrayList<>();

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

        if (isViewOnly) {
            loadSavedAnswers(quizId);
        }

        setupUI();
        displayQuestion();
    }

    private void loadSavedAnswers(int quizId) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.T_GIVEN_ANSWER, 
                new String[]{"questionId", "selectedOptionId"}, 
                "userId = ? AND quizId = ?", 
                new String[]{String.valueOf(sessionManager.getUserId()), String.valueOf(quizId)}, 
                null, null, null);
        
        while (c.moveToNext()) {
            int qId = c.getInt(0);
            int oId = c.getInt(1);
            if (!savedAnswers.containsKey(qId)) {
                savedAnswers.put(qId, new ArrayList<>());
            }
            savedAnswers.get(qId).add(oId);
        }
        c.close();
    }

    private void setupUI() {
        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonNext.setOnClickListener(v -> handleNextButtonClick());
    }

    private void displayQuestion() {
        hasAnsweredCorrectlyAll = false;
        hasAnsweredWrong = false;
        currentQuestionCorrectFound.clear();
        
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
        List<Integer> userSavedOptions = isViewOnly ? savedAnswers.get(question.getId()) : null;

        for (AnswerOption option : options) {
            addOptionView(option, userSavedOptions);
        }

        updateNextButtonText();
    }

    private void updateNextButtonText() {
        if (currentQuestionIndex == questionList.size() - 1) {
            binding.buttonNext.setText(isViewOnly ? "Chiudi" : "Termina");
        } else {
            binding.buttonNext.setText(isViewOnly ? "Avanti" : "Prossima Domanda");
        }
    }

    private void addOptionView(AnswerOption option, List<Integer> userOptions) {
        ItemAnswerOptionBinding optionBinding = ItemAnswerOptionBinding.inflate(LayoutInflater.from(this), binding.layoutOptions, false);
        optionBinding.textOption.setText(option.getText());
        optionBinding.getRoot().setTag(option);

        if (isViewOnly) {
            optionBinding.getRoot().setClickable(false);
            if (option.isCorrect()) {
                styleOptionCorrect(optionBinding);
                optionBinding.imageStatus.setVisibility(View.VISIBLE);
                optionBinding.imageStatus.setImageResource(android.R.drawable.checkbox_on_background);
            } else if (userOptions != null && userOptions.contains(option.getId())) {
                styleOptionWrong(optionBinding);
                optionBinding.imageStatus.setVisibility(View.VISIBLE);
                optionBinding.imageStatus.setImageResource(android.R.drawable.ic_delete);
            }
        } else {
            optionBinding.getRoot().setOnClickListener(v -> {
                if (!hasAnsweredCorrectlyAll && !hasAnsweredWrong) {
                    handleAnswerSelection(option, optionBinding);
                }
            });
        }

        binding.layoutOptions.addView(optionBinding.getRoot());
    }

    private void handleAnswerSelection(AnswerOption selectedOption, ItemAnswerOptionBinding selectedBinding) {
        Question currentQuestion = questionList.get(currentQuestionIndex);
        List<AnswerOption> allOptions = answerOptionDao.getByQuestionId(currentQuestion.getId());
        
        int totalCorrectInQuestion = 0;
        for (AnswerOption o : allOptions) if (o.isCorrect()) totalCorrectInQuestion++;

        // Registra la risposta
        if (!sessionAnswers.containsKey(currentQuestion.getId())) {
            sessionAnswers.put(currentQuestion.getId(), new ArrayList<>());
        }
        sessionAnswers.get(currentQuestion.getId()).add(selectedOption.getId());

        if (selectedOption.isCorrect()) {
            currentQuestionCorrectFound.add(selectedOption.getId());
            styleOptionCorrect(selectedBinding);
            selectedBinding.getRoot().setClickable(false); // Non cliccare due volte la stessa

            // Calcolo punti parziali
            double pointsPerCorrect = (double) currentQuiz.getPoints() / (questionList.size() * totalCorrectInQuestion);
            totalPointsEarned += pointsPerCorrect;

            if (currentQuestionCorrectFound.size() == totalCorrectInQuestion) {
                hasAnsweredCorrectlyAll = true;
                binding.buttonNext.setEnabled(true);
                binding.cardExplanation.setVisibility(View.VISIBLE);
            }
        } else {
            hasAnsweredWrong = true;
            styleOptionWrong(selectedBinding);
            highlightRemainingCorrectAnswers();
            binding.buttonNext.setEnabled(true);
            binding.cardExplanation.setVisibility(View.VISIBLE);
        }
    }

    private void highlightRemainingCorrectAnswers() {
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
        int finalScore = (int) Math.round(totalPointsEarned);
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int userId = sessionManager.getUserId();
        if (userId == -1) { finish(); return; }
        
        long now = System.currentTimeMillis();
        db.beginTransaction();
        try {
            ContentValues cvResult = new ContentValues();
            cvResult.put("userId", userId);
            cvResult.put("quizId", currentQuiz.getId());
            cvResult.put("score", finalScore);
            cvResult.put("date", now);
            db.insert(DBHelper.T_QUIZ_RESULT, null, cvResult);

            for (Map.Entry<Integer, List<Integer>> entry : sessionAnswers.entrySet()) {
                for (Integer optionId : entry.getValue()) {
                    ContentValues cvAnswer = new ContentValues();
                    cvAnswer.put("userId", userId);
                    cvAnswer.put("quizId", currentQuiz.getId());
                    cvAnswer.put("questionId", entry.getKey());
                    cvAnswer.put("selectedOptionId", optionId);
                    db.insert(DBHelper.T_GIVEN_ANSWER, null, cvAnswer);
                }
            }

            db.execSQL("INSERT OR IGNORE INTO " + DBHelper.T_USER_STATS + " (userId, totalQuizzes, totalPoints, weeklyChangePerc) VALUES (" + userId + ", 0, 0, 0)");
            db.execSQL("UPDATE " + DBHelper.T_USER_STATS + " SET totalPoints = totalPoints + " + finalScore + ", totalQuizzes = totalQuizzes + 1 WHERE userId = " + userId);

            db.setTransactionSuccessful();
            Toast.makeText(this, "Quiz completato! Punti: " + finalScore, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            finish();
        }
    }
}
