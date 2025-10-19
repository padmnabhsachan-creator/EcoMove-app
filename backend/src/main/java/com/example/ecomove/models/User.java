package com.example.ecomove.models;

import java.sql.Timestamp;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private int totalCreditPoints;
    private boolean isVerified;
    private Timestamp createdAt;
    private String password;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public int getTotalCreditPoints() { return totalCreditPoints; }
    public void setTotalCreditPoints(int totalCreditPoints) { this.totalCreditPoints = totalCreditPoints; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}