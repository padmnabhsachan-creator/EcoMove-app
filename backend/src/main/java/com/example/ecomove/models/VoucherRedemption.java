package com.example.ecomove.models;

import java.sql.Timestamp;

public class VoucherRedemption {

    private String redemptionId;
    private String userId;
    private Long voucherId;
    private Timestamp redeemedAt;

    // Getters and setters
    public String getRedemptionId() {
        return redemptionId;
    }

    public void setRedemptionId(String redemptionId) {
        this.redemptionId = redemptionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public Timestamp getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(Timestamp redeemedAt) {
        this.redeemedAt = redeemedAt;
    }
}