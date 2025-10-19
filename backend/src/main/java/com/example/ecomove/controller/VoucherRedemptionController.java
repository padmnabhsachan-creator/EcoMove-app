package com.example.ecomove.controller;

import com.example.ecomove.models.VoucherRedemption;
import com.example.ecomove.service.VoucherRedemptionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/redemptions")
public class VoucherRedemptionController {

    private final VoucherRedemptionService redemptionService;

    public VoucherRedemptionController(VoucherRedemptionService redemptionService) {
        this.redemptionService = redemptionService;
    }

    @PostMapping
public ResponseEntity<?> redeemVoucher(@RequestBody VoucherRedemption redemption) {
    try {
        VoucherRedemption result = redemptionService.redeemVoucher(redemption);
        return ResponseEntity.ok(result);
    } catch (RuntimeException e) {
        System.out.println("Redemption failed: " + e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    @GetMapping("/user/{userId}")
    public List<VoucherRedemption> getUserRedemptions(@PathVariable String userId) {
        return redemptionService.getRedemptionsByUser(userId);
    }
}