package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FavoriteResponse {

    private Long id;

    private Long userId;

    private Long foodId;
}