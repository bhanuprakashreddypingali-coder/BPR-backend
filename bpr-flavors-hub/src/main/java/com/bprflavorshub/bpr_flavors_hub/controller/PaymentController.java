package com.bprflavorshub.bpr_flavors_hub.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bprflavorshub.bpr_flavors_hub.dto.PaymentRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.PaymentResponse;
import com.bprflavorshub.bpr_flavors_hub.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    // =========================================================
    // MAKE PAYMENT
    // =========================================================

    @PostMapping
    public ResponseEntity<PaymentResponse> makePayment(
            Principal principal,
            @RequestBody PaymentRequest request) {

        return ResponseEntity.ok(
                paymentService.makePayment(
                        principal.getName(),
                        request));
    }

    // =========================================================
    // MY PAYMENTS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<PaymentResponse>>
            getMyPayments(
                    Principal principal) {

        return ResponseEntity.ok(
                paymentService.getMyPayments(
                        principal.getName()));
    }

    // =========================================================
    // PAYMENT BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse>
            getPayment(
                    Principal principal,
                    @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.getPaymentById(
                        principal.getName(),
                        id));
    }
}