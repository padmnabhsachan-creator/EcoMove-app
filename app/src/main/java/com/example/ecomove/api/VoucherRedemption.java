package com.example.ecomove.api;

public class VoucherRedemption {
    private String userId;
    private String voucherId;

    public VoucherRedemption(String userId, String voucherId) {
        this.userId = userId;
        this.voucherId = voucherId;
    }

    public String getUserId() { return userId; }
    public String getVoucherId() { return voucherId; }
}