package com.bprflavorshub.bpr_flavors_hub.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bprflavorshub.bpr_flavors_hub.dto.OrderResponse;
import com.bprflavorshub.bpr_flavors_hub.entity.Order;
import com.bprflavorshub.bpr_flavors_hub.entity.Restaurant;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.OrderRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.RestaurantRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            RestaurantRepository restaurantRepository) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // =========================================================
    // CUSTOMER - MY ORDERS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String identifier) {

        User user = findUser(identifier);

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapOrder)
                .collect(Collectors.toList());
    }

    // =========================================================
    // CUSTOMER - CANCEL OWN ORDER
    // =========================================================

    @Override
    public OrderResponse cancelMyOrder(
            String identifier,
            Long orderId) {

        User user = findUser(identifier);

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        if (orderId == null) {
            throw new RuntimeException("Order ID cannot be null.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found."));

        // Security check
        if (order.getUserId() == null
                || !order.getUserId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to cancel this order.");
        }

        String currentStatus = normalizeStatus(order.getStatus());

        if ("DELIVERED".equals(currentStatus)) {

            throw new RuntimeException(
                    "Delivered orders cannot be cancelled.");
        }

        if ("CANCELLED".equals(currentStatus)) {

            throw new RuntimeException(
                    "Order is already cancelled.");
        }

        order.setStatus("CANCELLED");

        Order savedOrder = orderRepository.save(order);

        return mapOrder(savedOrder);
    }

    // =========================================================
    // ADMIN - ALL ORDERS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::mapOrder)
                .collect(Collectors.toList());
    }

    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {

        if (id == null) {
            throw new RuntimeException(
                    "Order ID cannot be null.");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: " + id));

        return mapOrder(order);
    }

    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    @Override
    public OrderResponse updateOrderStatus(
            Long id,
            String status) {

        if (id == null) {
            throw new RuntimeException(
                    "Order ID cannot be null.");
        }

        if (status == null
                || status.trim().isEmpty()) {

            throw new RuntimeException(
                    "Order status cannot be empty.");
        }

        String newStatus = normalizeStatus(status);

        if (!isValidOrderStatus(newStatus)) {

            throw new RuntimeException(
                    "Invalid order status: " + newStatus);
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: " + id));

        String currentStatus =
                normalizeStatus(order.getStatus());

        // Delivered cannot be changed
        if ("DELIVERED".equals(currentStatus)
                && !"DELIVERED".equals(newStatus)) {

            throw new RuntimeException(
                    "Delivered order status cannot be changed.");
        }

        // Cancelled cannot be changed
        if ("CANCELLED".equals(currentStatus)
                && !"CANCELLED".equals(newStatus)) {

            throw new RuntimeException(
                    "Cancelled order status cannot be changed.");
        }

        order.setStatus(newStatus);

        Order savedOrder = orderRepository.save(order);

        return mapOrder(savedOrder);
    }

    // =========================================================
    // DELETE ORDER
    // =========================================================

    @Override
    public void deleteOrder(Long id) {

        if (id == null) {
            throw new RuntimeException(
                    "Order ID cannot be null.");
        }

        if (!orderRepository.existsById(id)) {

            throw new RuntimeException(
                    "Order not found with id: " + id);
        }

        orderRepository.deleteById(id);
    }

    // =========================================================
    // VALID ORDER STATUS
    // =========================================================

    private boolean isValidOrderStatus(String status) {

        return "PENDING".equals(status)
                || "ACCEPTED".equals(status)
                || "PREPARING".equals(status)
                || "OUT_FOR_DELIVERY".equals(status)
                || "DELIVERED".equals(status)
                || "CANCELLED".equals(status)
                || "PAID".equals(status);
    }

    // =========================================================
    // NORMALIZE STATUS
    // =========================================================

    private String normalizeStatus(String status) {

        if (status == null) {
            return "";
        }

        return status.trim().toUpperCase();
    }

    // =========================================================
    // FIND USER
    //
    // Your login uses PHONE.
    // Email is also supported for older accounts.
    // =========================================================

    private User findUser(String identifier) {

        if (identifier == null
                || identifier.trim().isEmpty()) {

            return null;
        }

        String value = identifier.trim();

        // First try phone
        User user = userRepository
                .findByPhone(value)
                .orElse(null);

        if (user != null) {
            return user;
        }

        // Then try email
        return userRepository
                .findByEmail(value)
                .orElse(null);
    }

    // =========================================================
    // ORDER MAPPER
    //
    // Gets:
    // Customer Name
    // Customer Phone
    // Restaurant Name
    // =========================================================

    private OrderResponse mapOrder(Order order) {

        if (order == null) {
            return null;
        }

        // -----------------------------------------------------
        // CUSTOMER INFORMATION
        // -----------------------------------------------------

        String customerName = "Not available";
        String customerPhone = "Not available";

        if (order.getUserId() != null) {

            User customer = userRepository
                    .findById(order.getUserId())
                    .orElse(null);

            if (customer != null) {

                // Your User entity has FULL NAME,
                // not NAME.
                if (customer.getFullName() != null
                        && !customer.getFullName()
                                .trim()
                                .isEmpty()) {

                    customerName =
                            customer.getFullName();
                }

                if (customer.getPhone() != null
                        && !customer.getPhone()
                                .trim()
                                .isEmpty()) {

                    customerPhone =
                            customer.getPhone();
                }
            }
        }

        // -----------------------------------------------------
        // RESTAURANT INFORMATION
        // -----------------------------------------------------

        String restaurantName = "Not available";

        if (order.getRestaurantId() != null) {

            Restaurant restaurant =
                    restaurantRepository
                            .findById(
                                    order.getRestaurantId())
                            .orElse(null);

            if (restaurant != null
                    && restaurant.getRestaurantName() != null
                    && !restaurant.getRestaurantName()
                            .trim()
                            .isEmpty()) {

                restaurantName =
                        restaurant.getRestaurantName();
            }
        }

        // -----------------------------------------------------
        // BUILD RESPONSE
        // -----------------------------------------------------

        return OrderResponse.builder()

                .id(order.getId())

                .userId(order.getUserId())

                .restaurantId(order.getRestaurantId())

                .foodId(order.getFoodId())

                .foodName(order.getFoodName())

                .quantity(order.getQuantity())

                .totalAmount(order.getTotalAmount())

                .deliveryAddress(order.getDeliveryAddress())

                .paymentMethod(order.getPaymentMethod())

                .status(order.getStatus())

                .createdAt(order.getCreatedAt())

                .customerName(customerName)

                .customerPhone(customerPhone)

                .restaurantName(restaurantName)

                .build();
    }
}