package com.wpi.helpme.com.wpi.helpme.database;

public class UserProfile {
    private String userId;
    private String userName;
    private String email;

    public UserProfile() {
        this.userId = "";
        this.email = "";
        this.userName = "";
    }

    public UserProfile(String userId, String email, String userName) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
    }

    public String getUserName() {

        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
