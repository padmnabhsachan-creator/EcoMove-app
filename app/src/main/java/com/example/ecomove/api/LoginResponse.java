package com.example.ecomove.api;

public class LoginResponse {
    private String userId;
    private String name;
    private String token;

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getToken() { return token; }
}