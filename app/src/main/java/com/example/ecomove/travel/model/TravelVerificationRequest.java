package com.example.ecomove.travel.model;

public class TravelVerificationRequest {

    private String verificationMethod = "QR"; // optional field
    private String userId;
    private String source;
    private String destination;
    private double distanceKm;
    private String travelDate; // Keep as String or convert to Date if needed
    private String mode;

    public TravelVerificationRequest() {
        // Default constructor
    }

    public TravelVerificationRequest(String userId, String source, String destination, double distanceKm, String travelDate, String mode) {
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.travelDate = travelDate;
        this.mode = mode;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public double getDistanceKm() { return distanceKm; }
    public String getTravelDate() { return travelDate; }
    public String getMode() { return mode; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setSource(String source) { this.source = source; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }
    public void setMode(String mode) { this.mode = mode; }

    public String getVerificationMethod() {
        return verificationMethod;
    }

    public void setVerificationMethod(String verificationMethod) {
        this.verificationMethod = verificationMethod;
    }
}