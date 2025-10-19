package com.example.ecomove.service;

import com.example.ecomove.models.Voucher;
import com.example.ecomove.service.mapper.VoucherRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class VoucherService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public VoucherService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Voucher> getActiveVouchers() {
        String query = "SELECT * FROM voucher WHERE expiry_date > ?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.query(query, new VoucherRowMapper(), now);
    }

public List<Voucher> getVouchersByUser(String userId) {
    String query = """
       SELECT v.* FROM voucher v
    WHERE (v.is_global = true OR v.user_id = ?)
    AND v.expiry_date > CURRENT_TIMESTAMP
    AND NOT EXISTS (
        SELECT 1 FROM voucher_redemptions r
        WHERE r.voucher_id = v.id AND r.user_id = ?
    )
    """;
    return jdbcTemplate.query(query, new VoucherRowMapper(), userId, userId);
}
    

public boolean redeemVoucher(String userId, Long voucherId) {
    // Fetch voucher (global or user-specific)
    String voucherQuery = "SELECT * FROM voucher WHERE id = ?";
    List<Voucher> vouchers = jdbcTemplate.query(voucherQuery, new VoucherRowMapper(), voucherId);
    if (vouchers.isEmpty()) return false;

    Voucher voucher = vouchers.get(0);

    // Check if already redeemed by this user
    String redemptionCheck = "SELECT COUNT(*) FROM voucher_redemptions WHERE user_id = ? AND voucher_id = ?";
    Integer count = jdbcTemplate.queryForObject(redemptionCheck, Integer.class, userId, voucherId);
    if (count != null && count > 0) return false;

    // Check user credit
    String creditQuery = "SELECT credit_points FROM user WHERE id = ?";
    Integer userCredits = jdbcTemplate.queryForObject(creditQuery, Integer.class, userId);
    if (userCredits == null || userCredits < voucher.getCreditPoints()) return false;

    // Deduct credit
    String updateUser = "UPDATE user SET credit_points = credit_points - ? WHERE id = ?";
    jdbcTemplate.update(updateUser, voucher.getCreditPoints(), userId);

    // Mark voucher as redeemed only if it's user-specific
    if (!voucher.isGlobal()) {
        String updateVoucher = "UPDATE voucher SET is_redeemed = true WHERE id = ?";
        jdbcTemplate.update(updateVoucher, voucherId);
    }

    // Log redemption
    String insertRedemption = "INSERT INTO voucher_redemptions (redemption_id, user_id, voucher_id, redeemed_at) VALUES (?, ?, ?, ?)";
    String redemptionId = "REDEEM_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    Timestamp now = new Timestamp(System.currentTimeMillis());
    jdbcTemplate.update(insertRedemption, redemptionId, userId, voucherId, now);

    return true;
}
}