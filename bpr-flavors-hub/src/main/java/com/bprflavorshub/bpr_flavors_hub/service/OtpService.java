package com.bprflavorshub.bpr_flavors_hub.service;

public interface OtpService {

    String sendOtp(String phone);

    boolean verifyOtp(String phone, String otp);
}