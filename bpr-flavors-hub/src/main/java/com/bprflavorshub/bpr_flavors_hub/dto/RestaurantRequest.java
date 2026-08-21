package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Data;

@Data
public class RestaurantRequest {

    private String restaurantName;
    private String ownerName;
    private String email;
    private String phone;
    private String address;
    private String image;
    private String description;
    private String openingTime;
    private String closingTime;
}