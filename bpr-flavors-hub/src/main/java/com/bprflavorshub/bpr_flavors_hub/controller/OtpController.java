package com.bprflavorshub.bpr_flavors_hub.controller;

import com.bprflavorshub.bpr_flavors_hub.dto.SendOtpRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.VerifyOtpRequest;
import com.bprflavorshub.bpr_flavors_hub.service.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin("*")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {

        this.otpService = otpService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(
            @RequestBody SendOtpRequest request) {

        return ResponseEntity.ok(
                otpService.sendOtp(request.getPhone()));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(
            @RequestBody VerifyOtpRequest request) {

        return ResponseEntity.ok(
                otpService.verifyOtp(
                        request.getPhone(),
                        request.getOtp()));
    }

}