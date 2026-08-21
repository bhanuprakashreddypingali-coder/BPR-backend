package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDashboardResponse {

    private long totalFoods;
    private long totalOrders;
    private long pendingOrders;
    private long completedOrders;

    private double totalRevenue;
    private long cancelledOrders;
    private long deliveredOrders;
}