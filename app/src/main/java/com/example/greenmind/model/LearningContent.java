package com.example.greenmind.model;
public class LearningContent {
    private int id;
    private String title;
    private String category;
    private int readingTimeMin;

    public LearningContent() {}

    public LearningContent(int id, String title, String category, int readingTimeMin) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.readingTimeMin = readingTimeMin;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getReadingTimeMin() { return readingTimeMin; }
    public void setReadingTimeMin(int readingTimeMin) { this.readingTimeMin = readingTimeMin; }
}
