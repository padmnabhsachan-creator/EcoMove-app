package com.example.ecomove.models;

import java.sql.Timestamp;


public class TravelVerification {

    private Long verificationId;
    private String userId;
    private Timestamp travelDate;
    private String source;
    private String destination;
    private double distanceKm;
    private Timestamp verifiedAt;
    private String mode;
    private String verificationMethod; // e.g., "QR" or "Manual"
    private int creditEarned;

   public int getCreditEarned() {
    return creditEarned;
   }

    public void setCreditEarned(int creditEarned) {
    this.creditEarned = creditEarned;
   }

    public String getVerificationMethod() {
    return verificationMethod;
    }

    public void setVerificationMethod(String verificationMethod) {
    this.verificationMethod = verificationMethod;
   }

    public String getMode() {
       return mode;
    }

    public void setMode(String mode) {
       this.mode = mode;
    }

    // Getters and setters
    public Long getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(Long verificationId) {
        this.verificationId = verificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(Timestamp travelDate) {
        this.travelDate = travelDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Timestamp getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Timestamp verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}