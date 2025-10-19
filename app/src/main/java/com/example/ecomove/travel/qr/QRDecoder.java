package com.example.ecomove.travel.qr;

import com.example.ecomove.travel.model.TravelVerificationRequest;

public class QRDecoder {
    public TravelVerificationRequest decode(String qrContent) {
        String[] parts = qrContent.split("\\|");
        if (parts.length != 6) throw new IllegalArgumentException("Invalid QR format");

        String userId = parts[0];
        String source = parts[1];
        String destination = parts[2];
        double distance = Double.parseDouble(parts[3]);
        String travelDate = parts[4];
        String mode = parts[5];

        TravelVerificationRequest info = new TravelVerificationRequest();
        info.setUserId(userId);
        info.setSource(source);
        info.setDestination(destination);
        info.setDistanceKm(distance);
        info.setTravelDate(travelDate);
        info.setMode(mode);

        return info;
    }
}