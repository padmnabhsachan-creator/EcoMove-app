package com.example.ecomove.service.mapper;

import org.springframework.lang.NonNull;
import com.example.ecomove.models.VoucherRedemption;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class VoucherRedemptionRowMapper implements RowMapper<VoucherRedemption> {
    @Override
    public VoucherRedemption mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        VoucherRedemption redemption = new VoucherRedemption();

        String redemptionId = rs.getString("redemption_id");
        if (redemptionId != null) {
        redemption.setRedemptionId(redemptionId);
}

        redemption.setUserId(rs.getString("user_id"));

        Object voucherIdObj = rs.getObject("voucher_id");
        if (voucherIdObj != null) {
            redemption.setVoucherId(((Number) voucherIdObj).longValue());
        }

        Timestamp redeemedAt = rs.getTimestamp("redeemed_at");
        if (redeemedAt != null) {
            redemption.setRedeemedAt(redeemedAt);
        }

        return redemption;
    }
}