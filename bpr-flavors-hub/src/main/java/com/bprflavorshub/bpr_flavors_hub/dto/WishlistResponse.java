package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponse {

    private Long wishlistId;

    private Long foodId;

    private String foodName;

    private String description;

    private Double price;

    private String image;

    private String category;

    private Boolean available;

    private Long restaurantId;

    private String restaurantName;

    private String restaurantImage;

    private Double restaurantRating;
}