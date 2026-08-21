package com.bprflavorshub.bpr_flavors_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerReportResponse {

    // Dashboard
    private long totalFoods;

    private long totalOrders;

    private long pendingOrders;

    private long completedOrders;

    private long cancelledOrders;

    private long deliveredOrders;

    // Revenue
    private double totalRevenue;

    private double todayRevenue;

    private double monthlyRevenue;

    // Restaurant
    private double averageRating;

    private long totalCustomers;
}