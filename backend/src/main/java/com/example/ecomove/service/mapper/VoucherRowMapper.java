package com.example.ecomove.service.mapper;

import org.springframework.lang.NonNull;
import com.example.ecomove.models.Voucher;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VoucherRowMapper implements RowMapper<Voucher> {
    @Override
    public Voucher mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        Voucher voucher = new Voucher();
        voucher.setId(rs.getLong("id"));
        voucher.setUserId(rs.getString("user_id"));
        voucher.setDescription(rs.getString("description"));
        voucher.setAmount(rs.getDouble("amount"));
        voucher.setVoucherName(rs.getString("voucher_name"));

        // credit_cost may be nullable in DB; read safely
        Integer creditCost = rs.getObject("credit_cost") != null ? rs.getInt("credit_cost") : null;
        voucher.setCreditCost(creditCost);

        voucher.setIssuedAt(rs.getTimestamp("issued_at"));
        voucher.setExpiryDate(rs.getTimestamp("expiry_date"));

        // Map redeemed flag
        voucher.setRedeemed(rs.getBoolean("is_redeemed"));

        return voucher;
    }
}