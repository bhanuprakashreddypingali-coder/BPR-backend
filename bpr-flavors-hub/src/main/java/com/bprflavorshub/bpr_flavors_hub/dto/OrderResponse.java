package com.bprflavorshub.bpr_flavors_hub.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    // =========================================================
    // ORDER
    // =========================================================

    private Long id;

    private Long userId;

    private Long restaurantId;

    private Long foodId;

    private String foodName;

    private Integer quantity;

    private Double totalAmount;

    // =========================================================
    // DELIVERY
    // =========================================================

    private String deliveryAddress;

    // =========================================================
    // PAYMENT
    // =========================================================

    private String paymentMethod;

    private String paymentStatus;

    // =========================================================
    // ORDER STATUS
    // =========================================================

    private String status;

    // =========================================================
    // CREATED
    // =========================================================

    private LocalDateTime createdAt;

    // =========================================================
    // CUSTOMER INFORMATION
    // =========================================================

    private String customerName;

    private String customerPhone;

    // =========================================================
    // RESTAURANT INFORMATION
    // =========================================================

    private String restaurantName;
}