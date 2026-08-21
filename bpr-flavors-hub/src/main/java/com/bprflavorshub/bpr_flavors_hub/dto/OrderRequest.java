package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Data;

@Data
public class OrderRequest {

    private Long userId;

    private Long foodId;

    private Integer quantity;

    private String deliveryAddress;

    private String paymentMethod;
}