package com.example.ecomove.travel.qr;

import org.springframework.stereotype.Component;
import com.example.ecomove.models.TravelVerification;
import java.sql.Timestamp;
import java.util.List;

@Component
public class QrDecoder {
   public TravelVerification decode(String qrContent) {
    System.out.println("📥 Raw QR content: " + qrContent);

    String[] parts = qrContent.split("\\|");
    System.out.println("🔍 Split parts length: " + parts.length);

    if (parts.length != 6) {
        System.err.println("❌ Expected 6 parts, got " + parts.length);
        return null;
    }

    try {
        String mode = parts[5].toLowerCase();
        List<String> validModes = List.of("bus", "train", "carpool");
        if (!validModes.contains(mode)) {
            System.err.println("❌ Unsupported travel mode: " + mode);
            throw new IllegalArgumentException("Unsupported travel mode: " + parts[5]);
        }

        double distance = Double.parseDouble(parts[3]);
        Timestamp travelDate = Timestamp.valueOf(parts[4]);

        TravelVerification verification = new TravelVerification();
        verification.setUserId(parts[0]);
        verification.setSource(parts[1]);
        verification.setDestination(parts[2]);
        verification.setDistanceKm(distance);
        verification.setTravelDate(travelDate);
        verification.setMode(mode);
        verification.setVerifiedAt(new Timestamp(System.currentTimeMillis()));

        System.out.println("✅ QR decoded successfully for user: " + parts[0]);
        return verification;
    } catch (Exception e) {
        System.err.println("❌ QR decoding failed: " + e.getMessage());
        return null;
    }
}
}