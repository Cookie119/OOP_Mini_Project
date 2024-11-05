package com.example.eventvista;

public class Request {
    private String name;
    private String email;
    private String key;
    private String status;

    // Default constructor required for calls to DataSnapshot.getValue(Request.class)
    public Request() {
    }

    public Request(String name, String email, String key, String status) {
        this.name = name;
        this.email = email;
        this.key = key;
        this.status = status;
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

    public String getKey() {
        return key;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
