package com.example.greenmind.resource.model;

public class Question {
    private int id;
    private int quizId;     // FK -> Quiz.id
    private String text;

    public Question() {}

    public Question(int id, int quizId, String text) {
        this.id = id;
        this.quizId = quizId;
        this.text = text;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
