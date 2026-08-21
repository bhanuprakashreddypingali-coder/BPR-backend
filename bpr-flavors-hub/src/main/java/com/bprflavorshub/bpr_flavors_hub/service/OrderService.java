package com.bprflavorshub.bpr_flavors_hub.service;

import java.util.List;

import com.bprflavorshub.bpr_flavors_hub.dto.OrderResponse;

public interface OrderService {

    // Customer
    List<OrderResponse> getMyOrders(String email);

    // Customer - cancel own order
    OrderResponse cancelMyOrder(String email, Long orderId);

    // Admin
    List<OrderResponse> getAllOrders();

    // Common
    OrderResponse getOrderById(Long id);

    // Admin / Owner
    OrderResponse updateOrderStatus(Long id, String status);

    // Admin
    void deleteOrder(Long id);
}