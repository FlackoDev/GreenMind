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
        
        String trimmedName = userName.trim();
        if (trimmedName.isEmpty()) return "??";
        
        String[] parts = trimmedName.split("\\s+");
        if (parts.length >= 2) {
            String p1 = parts[0];
            String p2 = parts[1];
            if (!p1.isEmpty() && !p2.isEmpty()) {
                return (p1.substring(0, 1) + p2.substring(0, 1)).toUpperCase();
            }
        }
        
        // Se c'è solo un nome o un problema con le parti, prendi le prime due lettere
        if (trimmedName.length() >= 2) {
            return trimmedName.substring(0, 2).toUpperCase();
        } else {
            return trimmedName.toUpperCase();
        }
    }
}
