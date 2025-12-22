package com.example.greenmind.resource.model;

public class Badge {
    private int id;
    private String name;
    private String description;
    private int requiredPoints;
    private boolean isSpecial;
    private boolean isUnlocked;

    public Badge() {}

    public Badge(int id, String name, String description, int requiredPoints) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredPoints = requiredPoints;
        this.isSpecial = false;
        this.isUnlocked = false;
    }

    public Badge(int id, String name, boolean isSpecial) {
        this.id = id;
        this.name = name;
        this.isSpecial = isSpecial;
        this.isUnlocked = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getRequiredPoints() { return requiredPoints; }
    public void setRequiredPoints(int requiredPoints) { this.requiredPoints = requiredPoints; }

    public boolean isSpecial() { return isSpecial; }
    public void setSpecial(boolean special) { isSpecial = special; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
}
