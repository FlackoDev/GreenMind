package com.example.greenmind.resource.model;

public class QuizResult {
    private long id;        // AUTOINCREMENT -> long
    private int userId;     // FK -> User.id
    private int quizId;     // FK -> Quiz.id
    private int score;
    private long date;      // timestamp (millis)

    public QuizResult() {}

    public QuizResult(long id, int userId, int quizId, int score, long date) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.date = date;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
}
