package com.example.greenmind.resource.model;

public class AnswerOption {
    private int id;
    private int questionId;   // FK -> Question.id
    private String text;
    private boolean isCorrect;

    public AnswerOption() {}

    public AnswerOption(int id, int questionId, String text, boolean isCorrect) {
        this.id = id;
        this.questionId = questionId;
        this.text = text;
        this.isCorrect = isCorrect;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
}
