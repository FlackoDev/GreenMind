package com.example.greenmind.resource.model;

public class Quiz {
    private int id;
    private String title;
    private String category;
    private String difficulty;
    private int points;
    private int numQuestions;

    public Quiz() {}

    public Quiz(int id, String title, String category, String difficulty, int points, int numQuestions) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.difficulty = difficulty;
        this.points = points;
        this.numQuestions = numQuestions;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getNumQuestions() { return numQuestions; }
    public void setNumQuestions(int numQuestions) { this.numQuestions = numQuestions; }
}
