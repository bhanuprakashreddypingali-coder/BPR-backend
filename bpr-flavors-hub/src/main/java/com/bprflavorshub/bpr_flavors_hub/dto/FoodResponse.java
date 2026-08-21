package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FoodResponse {

    private Long id;

    private String foodName;

    private String description;

    private Double price;

    private String image;

    private String category;

    private Boolean available;

    private Long restaurantId;

    private String restaurantName;

}