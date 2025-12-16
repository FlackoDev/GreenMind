package com.example.greenmind.resource.model;

public class UserStats {
    private int userId;            // PK + FK -> User.id
    private int totalQuizzes;
    private int totalPoints;
    private float weeklyChangePerc;

    public UserStats() {}

    public UserStats(int userId, int totalQuizzes, int totalPoints, float weeklyChangePerc) {
        this.userId = userId;
        this.totalQuizzes = totalQuizzes;
        this.totalPoints = totalPoints;
        this.weeklyChangePerc = weeklyChangePerc;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTotalQuizzes() { return totalQuizzes; }
    public void setTotalQuizzes(int totalQuizzes) { this.totalQuizzes = totalQuizzes; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public float getWeeklyChangePerc() { return weeklyChangePerc; }
    public void setWeeklyChangePerc(float weeklyChangePerc) { this.weeklyChangePerc = weeklyChangePerc; }
}
