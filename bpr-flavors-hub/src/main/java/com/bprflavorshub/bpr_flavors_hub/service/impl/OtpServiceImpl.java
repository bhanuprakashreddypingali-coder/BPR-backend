package com.bprflavorshub.bpr_flavors_hub.service.impl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.bprflavorshub.bpr_flavors_hub.entity.Otp;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.OtpRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.OtpService;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;

    public OtpServiceImpl(
            OtpRepository otpRepository,
            UserRepository userRepository) {

        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String sendOtp(String phone) {

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException("Phone number not registered"));

        String otp = String.format("%06d",
                new Random().nextInt(1000000));

        Otp otpEntity = Otp.builder()
                .phone(phone)
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();

        otpRepository.save(otpEntity);

        System.out.println("--------------------------------");
        System.out.println("OTP : " + otp);
        System.out.println("Phone : " + phone);
        System.out.println("--------------------------------");

        return "OTP Sent Successfully";
    }

    @Override
    public boolean verifyOtp(String phone, String otp) {

        Otp savedOtp = otpRepository
                .findTopByPhoneOrderByIdDesc(phone)
                .orElseThrow(() ->
                        new RuntimeException("OTP Not Found"));

        if (savedOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (!savedOtp.getOtp().equals(otp)) {
            return false;
        }

        savedOtp.setVerified(true);
        otpRepository.save(savedOtp);

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setPhoneVerified(true);
        userRepository.save(user);

        return true;
    }
}