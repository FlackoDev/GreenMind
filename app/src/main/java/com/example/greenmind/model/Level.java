package com.example.greenmind.model;

public class Level {
    private int id;
    private int number;
    private int requiredPoints;

    public Level() {}

    public Level(int id, int number, int requiredPoints) {
        this.id = id;
        this.number = number;
        this.requiredPoints = requiredPoints;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public int getRequiredPoints() { return requiredPoints; }
    public void setRequiredPoints(int requiredPoints) { this.requiredPoints = requiredPoints; }
}

