package com.example.greenmind.resource.quiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.greenmind.databinding.ItemQuizBinding;
import com.example.greenmind.resource.model.Quiz;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private final List<Quiz> quizList;
    private final OnQuizClickListener listener;

    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz);
    }

    public QuizAdapter(List<Quiz> quizList, OnQuizClickListener listener) {
        this.quizList = quizList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuizBinding binding = ItemQuizBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new QuizViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        holder.binding.textQuizTitle.setText(quiz.getTitle());
        holder.binding.textCategory.setText(quiz.getCategory());
        holder.binding.textDifficulty.setText(quiz.getDifficulty());
        holder.binding.textQuizInfo.setText(quiz.getNumQuestions() + " domande - " + quiz.getPoints() + " punti");

        holder.binding.buttonStartQuiz.setOnClickListener(v -> listener.onQuizClick(quiz));
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        final ItemQuizBinding binding;
        QuizViewHolder(ItemQuizBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
