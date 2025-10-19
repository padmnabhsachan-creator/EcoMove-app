package com.example.ecomove.api;

import java.io.Serializable;

public class UserResponse implements Serializable {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private int totalCreditPoints;
    private boolean verified;
    private String createdAt;
    private String token;

    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public int getTotalCreditPoints() { return totalCreditPoints; }
    public boolean isVerified() { return verified; }
    public String getCreatedAt() { return createdAt; }
}