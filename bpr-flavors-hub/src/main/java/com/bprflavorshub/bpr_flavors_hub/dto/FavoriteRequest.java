package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Data;

@Data
public class FavoriteRequest {

    private Long userId;

    private Long foodId;
}