package com.bprflavorshub.bpr_flavors_hub.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================================================
    // CUSTOMER
    // =========================================================

    private Long userId;

    // =========================================================
    // RESTAURANT
    // =========================================================

    private Long restaurantId;

    // =========================================================
    // FOOD
    // =========================================================

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

    /**
     * Payment status:
     *
     * PENDING
     * PAID
     * FAILED
     * REFUNDED
     */
    @Column(name = "payment_status")
    private String paymentStatus;

    // =========================================================
    // ORDER / DELIVERY STATUS
    // =========================================================

    /**
     * Order status:
     *
     * PENDING
     * CONFIRMED
     * PREPARING
     * OUT_FOR_DELIVERY
     * DELIVERED
     * CANCELLED
     */
    @Column(name = "status")
    private String status;

    // =========================================================
    // CREATED DATE
    // =========================================================

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // =========================================================
    // PRE PERSIST
    // =========================================================

    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null ||
                status.trim().isEmpty()) {

            status = "PENDING";
        }

        if (paymentStatus == null ||
                paymentStatus.trim().isEmpty()) {

            paymentStatus = "PENDING";
        }
    }
}