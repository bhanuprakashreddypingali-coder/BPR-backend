package com.bprflavorshub.bpr_flavors_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bprflavorshub.bpr_flavors_hub.entity.Payment;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(Long userId);

    Optional<Payment> findByOrderId(Long orderId);
}