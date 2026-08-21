package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Data;

@Data
public class CartRequest {

    private Long foodId;

    private Integer quantity;
}