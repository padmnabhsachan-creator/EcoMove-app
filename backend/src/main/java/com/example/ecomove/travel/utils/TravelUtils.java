package com.example.ecomove.travel.utils;



public class TravelUtils {
    public static double calculateSavedCO2(String mode, double distanceKm) {
    double baseline = 0.2; // solo car emissions per km
    double factor;

    switch (mode.toLowerCase()) {
        case "bus": factor = 0.05; break;
        case "train": factor = 0.04; break;
        case "carpool": factor = 0.07; break;
        default: factor = baseline;
    }

    return Math.max(0, (baseline - factor) * distanceKm);
}

   public static int calculateCreditPointsFromCO2(double savedCO2) {
    return (int) Math.round(savedCO2); // 1 credit per kg saved
}
}