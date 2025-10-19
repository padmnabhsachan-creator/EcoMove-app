package com.example.ecomove.models;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private int totalCreditPoints;
    private boolean isVerified;

    public User(String userId, String name, String email, String phoneNumber, int totalCreditPoints, boolean isVerified) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.totalCreditPoints = totalCreditPoints;
        this.isVerified = isVerified;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public int getTotalCreditPoints() { return totalCreditPoints; }
    public boolean isVerified() { return isVerified; }
}