package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {

    private Long restaurantId;

    private Integer rating;

    private String comment;
}