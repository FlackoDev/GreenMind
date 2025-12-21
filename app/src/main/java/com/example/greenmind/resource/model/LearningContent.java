package com.example.greenmind.resource.model;

public class LearningContent {
    private int id;
    private String title;
    private String category;
    private int readingTimeMin;
    private String preview;
    private String content;

    public LearningContent() {}

    public LearningContent(int id, String title, String category, int readingTimeMin, String preview, String content) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.readingTimeMin = readingTimeMin;
        this.preview = preview;
        this.content = content;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getReadingTimeMin() { return readingTimeMin; }
    public void setReadingTimeMin(int readingTimeMin) { this.readingTimeMin = readingTimeMin; }

    public String getPreview() { return preview; }
    public void setPreview(String preview) { this.preview = preview; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
