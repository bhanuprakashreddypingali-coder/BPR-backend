package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    private long totalUsers;

    private long totalRestaurants;

    private long totalFoods;

    private long totalOrders;

    private double totalRevenue;

    private long pendingOrders;
}