package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {

    private Long id;

    private Long orderId;

    private Double amount;

    private String paymentMethod;

    private String paymentStatus;

    private String transactionId;
}