package com.example.ecomove.models;

import java.sql.Timestamp;

public class Voucher {

    private Long id;
    private String userId;
    private String description;
    private Double amount;
    private String voucherName;
    private Integer creditCost;
    private Timestamp issuedAt;
    private Timestamp expiryDate;
    private boolean isGlobal;
    private boolean isRedeemed;

    public Integer getCreditPoints() {
        return creditCost;
    }

    
  public boolean isGlobal() {
    return isGlobal;
}



    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getVoucherName() {
        return voucherName;
    }

    public void setVoucherName(String voucherName) {
        this.voucherName = voucherName;
    }

    public Integer getCreditCost() {
        return creditCost;
    }

    public void setCreditCost(Integer creditCost) {
        this.creditCost = creditCost;
    }

    public Timestamp getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Timestamp issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isRedeemed() {
        return isRedeemed;
    }

    public void setRedeemed(boolean redeemed) {
        isRedeemed = redeemed;
    }

    @Override
    public String toString() {
        return "Voucher{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", voucherName='" + voucherName + '\'' +
                ", creditCost=" + creditCost +
                ", isRedeemed=" + isRedeemed +
                '}';
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.before(new Timestamp(System.currentTimeMillis()));
    }

    public boolean isRedeemable(int userCredits) {
        return !isRedeemed && !isExpired() && creditCost != null && userCredits >= creditCost;
    }
}