package com.example.greenmind.resource.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String role; // "user" o "admin"
    private String adminPinHash; // Hash del PIN per gli admin
    private long createdAt;

    public User() {}

    public User(int id, String name, String email, String passwordHash) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = "user";
    }

    public User(int id, String name, String email, String passwordHash, long createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.role = "user";
    }

    public User(int id, String name, String email, String passwordHash, String role, String adminPinHash, long createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.adminPinHash = adminPinHash;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAdminPinHash() { return adminPinHash; }
    public void setAdminPinHash(String adminPinHash) { this.adminPinHash = adminPinHash; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }
}
