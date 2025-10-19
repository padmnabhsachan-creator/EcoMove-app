package com.example.ecomove.service;

import com.example.ecomove.models.TravelVerification;
import com.example.ecomove.models.User;
import com.example.ecomove.travel.qr.QrDecoder;
import com.example.ecomove.travel.utils.TravelUtils;
import com.example.ecomove.service.mapper.TravelVerificationRowMapper;
import com.example.ecomove.service.mapper.UserRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class TravelVerificationService {

    private final JdbcTemplate jdbcTemplate;
    private final QrDecoder qrDecoder;

    @Autowired
    public TravelVerificationService(JdbcTemplate jdbcTemplate, QrDecoder qrDecoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.qrDecoder = qrDecoder;
    }

    public TravelVerification verifyTravel(TravelVerification verification) {
    if (verification == null || verification.getUserId() == null) {
        throw new IllegalArgumentException("Invalid travel verification data");
    }

    verification.setVerifiedAt(new Timestamp(System.currentTimeMillis()));

    // Calculate saved CO2 and credit points
    double savedCO2 = TravelUtils.calculateSavedCO2(verification.getMode(), verification.getDistanceKm());
    int points = TravelUtils.calculateCreditPointsFromCO2(savedCO2);
    verification.setCreditEarned(points); //  Store in travel log

    //  Save travel log with credit earned
    String insertSql = """
        INSERT INTO travel_verification (
            user_id, source, destination, distance_km, travel_date, verified_at, mode, verification_method, credit_earned
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;
try{
    jdbcTemplate.update(insertSql,
        verification.getUserId(),
        verification.getSource(),
        verification.getDestination(),
        verification.getDistanceKm(),
        verification.getTravelDate(),
        verification.getVerifiedAt(),
        verification.getMode(),
        verification.getVerificationMethod(),
        verification.getCreditEarned()
    );
        System.out.println("✅ Travel log inserted for user: " + verification.getUserId());
} catch (Exception e) {
    System.err.println("❌ Travel log insert failed: " + e.getMessage());
}


    //  Update user's total credit points
    rewardUser(verification);

    return verification;
}

    public TravelVerification verifyViaQr(String qrContent) {
        try {
            TravelVerification decoded = qrDecoder.decode(qrContent);
            if (decoded != null && decoded.getUserId() != null) {
                return verifyTravel(decoded);
            }
        } catch (Exception e) {
            // TODO: Add proper logging
            System.err.println("QR decoding failed: " + e.getMessage());
        }
        return null;
    }

    public List<TravelVerification> getTravelHistory(String userId) {
        String query = "SELECT * FROM travel_verification WHERE user_id = ?";
        return jdbcTemplate.query(query, new TravelVerificationRowMapper(), userId);
    }

    private void rewardUser(TravelVerification verification) {
      double savedCO2 = TravelUtils.calculateSavedCO2(verification.getMode(), verification.getDistanceKm());
      int points = TravelUtils.calculateCreditPointsFromCO2(savedCO2);

        String selectSql = "SELECT * FROM users WHERE user_id = ?";
       List<User> users = jdbcTemplate.query(selectSql, new UserRowMapper(), verification.getUserId());

        if (!users.isEmpty()) {
            User user = users.get(0);
            int newTotal = user.getTotalCreditPoints() + points;

            String updateSql = "UPDATE users SET total_credit_points = ? WHERE user_id = ?";
            jdbcTemplate.update(updateSql, newTotal, user.getUserId());
        } else {
            // TODO: Add proper logging
            System.err.println("User not found for ID: " + verification.getUserId());
        }
    }
}