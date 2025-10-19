package com.example.ecomove.service;

import com.example.ecomove.models.VoucherRedemption;
import com.example.ecomove.service.mapper.VoucherRedemptionRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class VoucherRedemptionService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public VoucherRedemptionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getUserCredits(String userId) {
        String query = "SELECT total_credit_points FROM users WHERE user_id = ?";
        Integer credits = jdbcTemplate.queryForObject(query, Integer.class, userId);
        return credits != null ? credits : 0;
    }

    public int getVoucherCost(Long voucherId) {
        String query = "SELECT credit_cost FROM voucher WHERE id = ?";
        Integer cost = jdbcTemplate.queryForObject(query, Integer.class, voucherId);
        return cost != null ? cost : Integer.MAX_VALUE;
    }

    public boolean hasAlreadyRedeemed(String userId, Long voucherId) {
        String query = "SELECT COUNT(*) FROM voucher_redemptions WHERE user_id = ? AND voucher_id = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, userId, voucherId);
        return count != null && count > 0;
    }

    @Transactional
    public VoucherRedemption redeemVoucher(VoucherRedemption redemption) {
        String userId = redemption.getUserId();
        Long voucherId = redemption.getVoucherId();

        if (voucherId == null) {
            throw new RuntimeException("voucherId is required");
        }

        if (hasAlreadyRedeemed(userId, voucherId)) {
            throw new RuntimeException("Voucher already redeemed by this user");
        }

        int userCredits = getUserCredits(userId);
        int voucherCost = getVoucherCost(voucherId);

        if (userCredits < voucherCost) {
            throw new RuntimeException("Insufficient credits for redemption");
        }

       redemption.setRedeemedAt(new Timestamp(System.currentTimeMillis()));
       String redemptionId = "REDEEM_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
       String insertSql = "INSERT INTO voucher_redemptions (redemption_id, user_id, voucher_id, redeemed_at) VALUES (?, ?, ?, ?)";
       jdbcTemplate.update(insertSql, redemptionId, userId, voucherId, redemption.getRedeemedAt());
        String updateSql = "UPDATE users SET total_credit_points = total_credit_points - ? WHERE user_id = ?";
        jdbcTemplate.update(updateSql, voucherCost, userId);
       redemption.setRedemptionId(redemptionId);
        return redemption;
    }

    public List<VoucherRedemption> getRedemptionsByUser(String userId) {
        String query = "SELECT * FROM voucher_redemptions WHERE user_id = ?";
        return jdbcTemplate.query(query, new VoucherRedemptionRowMapper(), userId);
    }
}