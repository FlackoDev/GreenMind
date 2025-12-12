package com.example.greenmind.model;

public class Quiz {
    private int id;
    private String title;
    private String category;
    private String difficulty;

    public Quiz() {}

    public Quiz(int id, String title, String category, String difficulty) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.difficulty = difficulty;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}
