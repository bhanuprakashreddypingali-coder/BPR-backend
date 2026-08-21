package com.bprflavorshub.bpr_flavors_hub.service;

import java.util.List;

import com.bprflavorshub.bpr_flavors_hub.dto.*;

public interface OwnerService {

    OwnerDashboardResponse getDashboard(String email);

    OwnerReportResponse getReports(String email);

    RestaurantResponse getRestaurant(String email);

    RestaurantResponse updateRestaurant(String email, RestaurantRequest request);

    List<FoodResponse> getFoods(String email);

    FoodResponse addFood(String email, FoodRequest request);

    FoodResponse updateFood(String email, Long foodId, FoodRequest request);

    void deleteFood(String email, Long foodId);

    List<OrderResponse> getOrders(String email);

    OrderResponse updateOrderStatus(String email, Long orderId, String status);
}