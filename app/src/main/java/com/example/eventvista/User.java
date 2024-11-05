package com.example.eventvista;

public class User {
    private String userId;
    private String name;
    private String email;
    private String roleStatus; // Assuming this is used for Event Manager role


    // Default constructor (required for calls to DataSnapshot.getValue(User.class))
    public User() {
        // Default values can be set here if necessary
    }

    // Parameterized constructor to initialize the fields
    public User(String userId, String name, String email, String roleStatus) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.roleStatus = roleStatus; // This could represent "Event Manager" or other roles
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleStatus() {
        return roleStatus;
    }

    public void setRoleStatus(String roleStatus) {
        this.roleStatus = roleStatus;
    }

}
