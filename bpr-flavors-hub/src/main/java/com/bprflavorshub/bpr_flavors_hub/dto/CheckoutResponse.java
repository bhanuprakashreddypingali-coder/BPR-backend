package com.bprflavorshub.bpr_flavors_hub.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {

    private String message;

    private Integer totalItems;

    private Double totalAmount;

    private List<Long> orderIds;
}