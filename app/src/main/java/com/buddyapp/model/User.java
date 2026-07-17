package com.buddyapp.model;

public class User {

    private int userId;
    private String username;
    private String passwordHash;
    private String salt;

    public User(int userId, String username, String passwordHash, String salt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public User(String username, String passwordHash, String salt) {
        this(0, username, passwordHash, salt);
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }
}
