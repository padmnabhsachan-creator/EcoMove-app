package com.example.ecomove.service.mapper;

import com.example.ecomove.models.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setUserId(rs.getString("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setTotalCreditPoints(rs.getInt("total_credit_points"));
        user.setVerified(rs.getBoolean("is_verified"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setPassword(rs.getString("password"));
        return user;
    }
}