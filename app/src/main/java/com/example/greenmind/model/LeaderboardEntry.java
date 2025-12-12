package com.example.greenmind.model;

public class LeaderboardEntry {
    private long id;      // AUTOINCREMENT
    private int userId;   // FK -> User.id
    private int points;
    private int position;

    public LeaderboardEntry() {}

    public LeaderboardEntry(long id, int userId, int points, int position) {
        this.id = id;
        this.userId = userId;
        this.points = points;
        this.position = position;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}

