package com.wpi.helpme.com.wpi.helpme.database;

public class UserProfile {
    private String userName;
    private String email;

    public UserProfile(String email, String userName) {

        this.email = email;
        this.userName = userName;
    }

    public String getUserName() {

        return userName;
    }

    public String getEmail() {
        return email;
    }
}
