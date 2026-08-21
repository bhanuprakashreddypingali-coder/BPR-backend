package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponse {

    private Long id;

    private Long foodId;

    private String foodName;

    private Double price;

    private Integer quantity;

    private Double total;
}