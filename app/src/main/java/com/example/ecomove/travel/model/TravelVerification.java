package com.example.ecomove.travel.model;

import java.util.Date;

public class TravelVerification {
    private String mode;
    private String source;
    private String destination;
    private double distanceKm;
    private Date travelDate;
    private Date verifiedAt;
    private int creditEarned;

    public int getCreditEarned() {
        return creditEarned;
    }

    public void setCreditEarned(int creditEarned) {
        this.creditEarned = creditEarned;
    }
    public String getMode() { return mode; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public double getDistanceKm() { return distanceKm; }
    public Date getTravelDate() { return travelDate; }
    public Date getVerifiedAt() { return verifiedAt; }

    // Setters
    public void setMode(String mode) { this.mode = mode; }
    public void setSource(String source) { this.source = source; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    public void setTravelDate(Date travelDate) { this.travelDate = travelDate; }
    public void setVerifiedAt(Date verifiedAt) { this.verifiedAt = verifiedAt; }
}