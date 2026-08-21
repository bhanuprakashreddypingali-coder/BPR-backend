package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Data;

@Data
public class CheckoutRequest {

    private String deliveryAddress;

    private String paymentMethod;

}