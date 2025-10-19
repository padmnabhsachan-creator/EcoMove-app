package com.example.ecomove.service;

import com.example.ecomove.models.User;
import com.example.ecomove.service.mapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> getUserById(String userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(query, new UserRowMapper(), userId);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public Optional<User> getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        List<User> users = jdbcTemplate.query(query, new UserRowMapper(), email);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public User createUser(User user) {
        if (user.getUserId() == null || user.getUserId().isBlank()) {
            user.setUserId(UUID.randomUUID().toString());
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        if (user.getTotalCreditPoints() == 0) {
            user.setTotalCreditPoints(0);
        }
        if (!user.isVerified()) {
            user.setVerified(true);
        }

        String insertSql = """
            INSERT INTO users (user_id, name, email, phone_number, total_credit_points, is_verified, created_at, password)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(insertSql,
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getTotalCreditPoints(),
                user.isVerified(),
                user.getCreatedAt(),
                user.getPassword());

        return user;
    }

    public Optional<User> loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), email, password);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public void updateCreditPoints(String userId, int delta) {
        Optional<User> userOpt = getUserById(userId);
        userOpt.ifPresent(user -> {
            int newTotal = user.getTotalCreditPoints() + delta;
            String updateSql = "UPDATE users SET total_credit_points = ? WHERE user_id = ?";
            jdbcTemplate.update(updateSql, newTotal, userId);
        });
    }
}