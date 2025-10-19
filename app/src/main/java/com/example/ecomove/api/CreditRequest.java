package com.example.ecomove.api;

public class CreditRequest {
    private int credits;

    public CreditRequest(int credits) {
        this.credits = credits;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}