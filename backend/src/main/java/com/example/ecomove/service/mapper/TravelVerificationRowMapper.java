package com.example.ecomove.service.mapper;

import org.springframework.lang.NonNull;
import com.example.ecomove.models.TravelVerification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TravelVerificationRowMapper implements RowMapper<TravelVerification> {
    @Override
    public TravelVerification mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        TravelVerification tv = new TravelVerification();
        tv.setVerificationMethod(rs.getString("verification_method"));
        tv.setVerificationId(rs.getLong("verification_id"));
        tv.setUserId(rs.getString("user_id"));
        tv.setSource(rs.getString("source"));
        tv.setDestination(rs.getString("destination"));
        tv.setDistanceKm(rs.getDouble("distance_km"));
        tv.setTravelDate(rs.getTimestamp("travel_date"));
        tv.setVerifiedAt(rs.getTimestamp("verified_at"));
        tv.setMode(rs.getString("mode"));
        tv.setCreditEarned(rs.getInt("credit_earned"));
        return tv;
    }
}