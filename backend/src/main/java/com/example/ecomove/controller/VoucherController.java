package com.example.ecomove.controller;

import com.example.ecomove.models.Voucher;
import com.example.ecomove.service.VoucherService;
import com.example.ecomove.models.VoucherRedemption;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    // GET /vouchers/active
    @GetMapping("/active")
    public List<Voucher> getActiveVouchers() {
        return voucherService.getActiveVouchers();
    }

    // GET /vouchers/user/{userId}
    @GetMapping("/user/{userId}")
    public List<Voucher> getVouchersByUser(@PathVariable String userId) {
        System.out.println("Fetching vouchers for user: " + userId);
        return voucherService.getVouchersByUser(userId);
        
    }

    // POST /vouchers/redeem
    @PostMapping("/redeem")
    public ResponseEntity<VoucherRedemption> redeemVoucher(@RequestBody VoucherRedemption redemption) {
        System.out.println("Redeem request received: userId=" + redemption.getUserId() + ", voucherId=" + redemption.getVoucherId());
        boolean success = voucherService.redeemVoucher(redemption.getUserId(), redemption.getVoucherId());
        if (success) {
            return ResponseEntity.ok(redemption);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}