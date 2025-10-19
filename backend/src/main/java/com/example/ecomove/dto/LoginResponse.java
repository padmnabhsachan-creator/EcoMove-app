package com.example.ecomove.dto;

public class LoginResponse {
    private String userId;
    private String name;
    private String token;

    public LoginResponse(String userId, String name, String token) {
        this.userId = userId;
        this.name = name;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }
}