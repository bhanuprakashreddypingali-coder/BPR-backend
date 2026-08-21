package com.bprflavorshub.bpr_flavors_hub.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;

    private Long restaurantId;

    private String restaurantName;

    private Long userId;

    private String customerName;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}