package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Data;

@Data
public class FoodRequest {

    private String foodName;

    private String description;

    private Double price;

    private String image;

    private String category;

    private Boolean available;

    private Long restaurantId;

}