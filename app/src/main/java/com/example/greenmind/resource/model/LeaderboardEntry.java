package com.example.greenmind.resource.model;

public class LeaderboardEntry {
    private int userId;
    private String userName;
    private int points;
    private int position;

    public LeaderboardEntry() {}

    public LeaderboardEntry(int userId, String userName, int points, int position) {
        this.userId = userId;
        this.userName = userName;
        this.points = points;
        this.position = position;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    
    public String getInitials() {
        if (userName == null || userName.isEmpty()) return "??";
        String[] parts = userName.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
        return userName.substring(0, Math.min(2, userName.length())).toUpperCase();
    }
}
