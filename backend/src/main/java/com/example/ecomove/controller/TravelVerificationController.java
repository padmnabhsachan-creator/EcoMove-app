package com.example.ecomove.controller;

import com.example.ecomove.models.TravelVerification;
import com.example.ecomove.service.TravelVerificationService;
import com.example.ecomove.travel.qr.QrDecoder;
import java.sql.Timestamp;
import jakarta.annotation.PostConstruct;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/travel")

public class TravelVerificationController {
    private final QrDecoder qrDecoder;
    private final TravelVerificationService travelService;

   public TravelVerificationController(TravelVerificationService travelService, QrDecoder qrDecoder) {
    this.travelService = travelService;
    this.qrDecoder = qrDecoder;
}

    @PostConstruct
    public void init() {
        System.out.println("✅ TravelVerificationController loaded");
    }

@PostMapping("/verify")
public TravelVerification verifyTravel(@RequestBody Map<String, Object> payload) {
    try {
        String userId = (String) payload.get("userId");
        String source = (String) payload.get("source");
        String destination = (String) payload.get("destination");
        double distanceKm = Double.parseDouble(payload.get("distanceKm").toString());
        String travelDateStr = (String) payload.get("travelDate");
        String mode = ((String) payload.get("mode")).toLowerCase();

        Timestamp travelDate = Timestamp.valueOf(travelDateStr);

        String qrLikePayload = String.join("|",
            userId, source, destination,
            String.valueOf(distanceKm),
            travelDate.toString().substring(0, 19),
            mode
        );

        TravelVerification verification = qrDecoder.decode(qrLikePayload);
        verification.setVerificationMethod("Manual");
        verification.setTravelDate(travelDate);
        return travelService.verifyTravel(verification);
    } catch (Exception e) {
        System.err.println("❌ Manual entry failed: " + e.getMessage());
        throw new IllegalArgumentException("Invalid manual entry format");
    }
}

  @PostMapping("/verify/qr")
public TravelVerification verifyViaQr(@RequestBody String qrContent) {
    System.out.println("📥 Received QR content: " + qrContent);

    TravelVerification decoded = qrDecoder.decode(qrContent);
    if (decoded != null) {
        decoded.setVerificationMethod("QR");
        return travelService.verifyTravel(decoded);
    }

    System.err.println("❌ Decoding failed for: " + qrContent);
    throw new IllegalArgumentException("Invalid QR content");
}

    @GetMapping("/user/{userId}")
    public List<TravelVerification> getUserTravelHistory(@PathVariable String userId) {
        return travelService.getTravelHistory(userId);
    }

    @GetMapping("/test-qr")
public ResponseEntity<?> testQr(@RequestParam String qr) {
    TravelVerification decoded = qrDecoder.decode(qr);
    if (decoded == null) {
        return ResponseEntity.badRequest().body("Invalid or unsupported QR content");
    }
    return ResponseEntity.ok(decoded);
}

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}