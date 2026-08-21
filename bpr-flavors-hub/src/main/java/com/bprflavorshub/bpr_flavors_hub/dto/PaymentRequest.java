package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Data;

@Data
public class PaymentRequest {

    private Long orderId;

    private String paymentMethod;
}