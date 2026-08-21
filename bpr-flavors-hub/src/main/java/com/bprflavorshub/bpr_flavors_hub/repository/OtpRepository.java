package com.bprflavorshub.bpr_flavors_hub.repository;

import com.bprflavorshub.bpr_flavors_hub.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findTopByPhoneOrderByIdDesc(String phone);

}