package com.bprflavorshub.bpr_flavors_hub.service.impl;

import org.springframework.stereotype.Service;

import com.bprflavorshub.bpr_flavors_hub.dto.AdminDashboardResponse;
import com.bprflavorshub.bpr_flavors_hub.repository.FoodRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.OrderRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.RestaurantRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodRepository foodRepository;
    private final OrderRepository orderRepository;

    public AdminServiceImpl(
            UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            FoodRepository foodRepository,
            OrderRepository orderRepository) {

        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodRepository = foodRepository;
        this.orderRepository = orderRepository;
    }

    // =========================================================
    // ADMIN DASHBOARD
    // =========================================================

    @Override
    public AdminDashboardResponse getDashboard() {

        // Total registered users
        long totalUsers = userRepository.count();

        // Total restaurants
        long totalRestaurants = restaurantRepository.count();

        // Total foods
        long totalFoods = foodRepository.count();

        // Total orders
        long totalOrders = orderRepository.count();

        // Total revenue
        Double revenue = orderRepository.sumRevenue();

        double totalRevenue = 0.0;

        if (revenue != null) {
            totalRevenue = revenue;
        }

        // Pending orders
        long pendingOrders =
                orderRepository.countByStatus("PENDING");

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalRestaurants(totalRestaurants)
                .totalFoods(totalFoods)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .pendingOrders(pendingOrders)
                .build();
    }
}