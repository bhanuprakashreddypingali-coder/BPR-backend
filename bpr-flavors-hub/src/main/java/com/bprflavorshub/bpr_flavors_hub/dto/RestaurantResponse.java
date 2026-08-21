package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantResponse {

    private Long id;
    private String restaurantName;
    private String ownerName;
    private String email;
    private String phone;
    private String address;
    private String image;
    private String description;
    private String openingTime;
    private String closingTime;
    private Double rating;
}