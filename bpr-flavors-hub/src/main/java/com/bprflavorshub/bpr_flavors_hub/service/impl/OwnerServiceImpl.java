package com.bprflavorshub.bpr_flavors_hub.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bprflavorshub.bpr_flavors_hub.dto.FoodRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.FoodResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.OrderResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.OwnerDashboardResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.OwnerReportResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.RestaurantRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.RestaurantResponse;

import com.bprflavorshub.bpr_flavors_hub.entity.Food;
import com.bprflavorshub.bpr_flavors_hub.entity.Order;
import com.bprflavorshub.bpr_flavors_hub.entity.Restaurant;
import com.bprflavorshub.bpr_flavors_hub.entity.User;

import com.bprflavorshub.bpr_flavors_hub.repository.FoodRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.OrderRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.RestaurantRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;

import com.bprflavorshub.bpr_flavors_hub.service.OwnerService;

@Service
@Transactional
public class OwnerServiceImpl implements OwnerService {

    private final FoodRepository foodRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public OwnerServiceImpl(
            FoodRepository foodRepository,
            OrderRepository orderRepository,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository) {

        this.foodRepository = foodRepository;
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // FIND OWNER
    // =========================================================

    private User findOwner(String identifier) {

        if (identifier == null ||
                identifier.trim().isEmpty()) {

            throw new RuntimeException(
                    "Owner identifier cannot be empty.");
        }

        String value =
                identifier.trim();

        User owner =
                userRepository
                        .findByPhone(value)
                        .orElse(null);

        if (owner == null) {

            owner =
                    userRepository
                            .findByEmail(value)
                            .orElse(null);
        }

        if (owner == null) {

            throw new RuntimeException(
                    "Owner not found.");
        }

        if (owner.getRole() == null ||
                !"RESTAURANT_OWNER".equalsIgnoreCase(
                        owner.getRole().name())) {

            throw new RuntimeException(
                    "User is not a restaurant owner.");
        }

        return owner;
    }

    // =========================================================
    // GET OWNER RESTAURANT
    // =========================================================

    private Restaurant getOwnerRestaurant(
            String identifier) {

        User owner =
                findOwner(identifier);

        return restaurantRepository
                .findByOwnerId(owner.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Restaurant not found for this owner."));
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public OwnerDashboardResponse getDashboard(
            String identifier) {

        Restaurant restaurant =
                getOwnerRestaurant(identifier);

        List<Order> orders =
                orderRepository.findByRestaurantId(
                        restaurant.getId());

        long totalFoods =
                foodRepository
                        .findByRestaurantId(
                                restaurant.getId())
                        .size();

        long totalOrders =
                orders.size();

        long pendingOrders =
                orders.stream()
                        .filter(order ->
                                isPendingStatus(
                                        order.getStatus()))
                        .count();

        long completedOrders =
                orders.stream()
                        .filter(order ->
                                isDeliveredStatus(
                                        order.getStatus()))
                        .count();

        long cancelledOrders =
                orders.stream()
                        .filter(order ->
                                "CANCELLED".equalsIgnoreCase(
                                        normalizeStatus(
                                                order.getStatus())))
                        .count();

        long deliveredOrders =
                orders.stream()
                        .filter(order ->
                                "DELIVERED".equalsIgnoreCase(
                                        normalizeStatus(
                                                order.getStatus())))
                        .count();

        double totalRevenue =
                orders.stream()
                        .filter(order ->
                                isDeliveredStatus(
                                        order.getStatus()))
                        .filter(order ->
                                order.getTotalAmount() != null)
                        .mapToDouble(
                                Order::getTotalAmount)
                        .sum();

        return OwnerDashboardResponse.builder()

                .totalFoods(totalFoods)

                .totalOrders(totalOrders)

                .pendingOrders(pendingOrders)

                .completedOrders(completedOrders)

                .cancelledOrders(cancelledOrders)

                .deliveredOrders(deliveredOrders)

                .totalRevenue(totalRevenue)

                .build();
    }

    // =========================================================
    // REPORTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public OwnerReportResponse getReports(
            String identifier) {

        Restaurant restaurant =
                getOwnerRestaurant(identifier);

        List<Order> orders =
                orderRepository.findByRestaurantId(
                        restaurant.getId());

        long totalFoods =
                foodRepository
                        .findByRestaurantId(
                                restaurant.getId())
                        .size();

        long totalOrders =
                orders.size();

        long pendingOrders =
                orders.stream()
                        .filter(order ->
                                isPendingStatus(
                                        order.getStatus()))
                        .count();

        long completedOrders =
                orders.stream()
                        .filter(order ->
                                isDeliveredStatus(
                                        order.getStatus()))
                        .count();

        long cancelledOrders =
                orders.stream()
                        .filter(order ->
                                "CANCELLED".equalsIgnoreCase(
                                        normalizeStatus(
                                                order.getStatus())))
                        .count();

        long deliveredOrders =
                orders.stream()
                        .filter(order ->
                                "DELIVERED".equalsIgnoreCase(
                                        normalizeStatus(
                                                order.getStatus())))
                        .count();

        double totalRevenue =
                orders.stream()
                        .filter(order ->
                                isDeliveredStatus(
                                        order.getStatus()))
                        .filter(order ->
                                order.getTotalAmount() != null)
                        .mapToDouble(
                                Order::getTotalAmount)
                        .sum();

        double rating =
                restaurant.getRating() == null
                        ? 0.0
                        : restaurant.getRating();

        return OwnerReportResponse.builder()

                .totalFoods(totalFoods)

                .totalOrders(totalOrders)

                .pendingOrders(pendingOrders)

                .completedOrders(completedOrders)

                .cancelledOrders(cancelledOrders)

                .deliveredOrders(deliveredOrders)

                .totalRevenue(totalRevenue)

                .todayRevenue(0.0)

                .monthlyRevenue(0.0)

                .averageRating(rating)

                .totalCustomers(0)

                .build();
    }

    // =========================================================
    // RESTAURANT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurant(
            String identifier) {

        Restaurant restaurant =
                getOwnerRestaurant(identifier);

        return mapRestaurant(restaurant);
    }

    // =========================================================
    // UPDATE RESTAURANT
    // =========================================================

    @Override
    public RestaurantResponse updateRestaurant(
            String identifier,
            RestaurantRequest request) {

        Restaurant restaurant =
                getOwnerRestaurant(identifier);

        if (request == null) {

            throw new RuntimeException(
                    "Restaurant request cannot be null.");
        }

        if (request.getRestaurantName() != null) {

            restaurant.setRestaurantName(
                    request.getRestaurantName());
        }

        if (request.getOwnerName() != null) {

            restaurant.setOwnerName(
                    request.getOwnerName());
        }

        if (request.getEmail() != null) {

            restaurant.setEmail(
                    request.getEmail());
        }

        if (request.getPhone() != null) {

            restaurant.setPhone(
                    request.getPhone());
        }

        if (request.getAddress() != null) {

            restaurant.setAddress(
                    request.getAddress());
        }

        if (request.getImage() != null) {

            restaurant.setImage(
                    request.getImage());
        }

        if (request.getDescription() != null) {

            restaurant.setDescription(
                    request.getDescription());
        }

        if (request.getOpeningTime() != null) {

            restaurant.setOpeningTime(
                    request.getOpeningTime());
        }

        if (request.getClosingTime() != null) {

            restaurant.setClosingTime(
                    request.getClosingTime());
        }

        return mapRestaurant(
                restaurantRepository.save(
                        restaurant));
    }

    // =========================================================
    // GET FOODS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<FoodResponse> getFoods(
            String identifier) {

        Restaurant restaurant =
                getOwnerRestaurant(identifier);

        return foodRepository
                .findByRestaurantId(
                        restaurant.getId())
                .stream()
                .map(this::mapFood)
                .collect(
                        Collectors.toList());
    }

    // =========================================================
    // ADD FOOD
    // =========================================================

    @Override
    public FoodResponse addFood(
            String identifier,
            FoodRequest request) {

        Restaurant restaurant =
                getOwnerRestaurant(identifier);

        if (request == null) {

            throw new RuntimeException(
                    "Food request cannot be null.");
        }

        Food food =
                Food.builder()

                        .foodName(
                                request.getFoodName())

                        .description(
                                request.getDescription())

                        .price(
                                request.getPrice())

                        .image(
                                request.getImage())

                        .category(
                                request.getCategory())

                        .available(
                                request.getAvailable())

                        .restaurant(
                                restaurant)

                        .build();

        food =
                foodRepository.save(
                        food);

        return mapFood(food);
    }

    // =========================================================
    // UPDATE FOOD
    // =========================================================

    @Override
    public FoodResponse updateFood(
            String identifier,
            Long foodId,
            FoodRequest request) {

        Restaurant restaurant =
                getOwnerRestaurant(identifier);

        if (foodId == null) {

            throw new RuntimeException(
                    "Food ID cannot be null.");
        }

        if (request == null) {

            throw new RuntimeException(
                    "Food request cannot be null.");
        }

        Food food =
                foodRepository.findById(
                        foodId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Food not found."));

        if (food.getRestaurant() == null ||
                food.getRestaurant().getId() == null ||
                !food.getRestaurant()
                        .getId()
                        .equals(
                                restaurant.getId())) {

            throw new RuntimeException(
                    "You are not allowed to update this food.");
        }

        food.setFoodName(
                request.getFoodName());

        food.setDescription(
                request.getDescription());

        food.setPrice(
                request.getPrice());

        food.setImage(
                request.getImage());

        food.setCategory(
                request.getCategory());

        food.setAvailable(
                request.getAvailable());

        return mapFood(
                foodRepository.save(
                        food));
    }

    // =========================================================
    // DELETE FOOD
    // =========================================================

    @Override
    public void deleteFood(
            String identifier,
            Long foodId) {

        Restaurant restaurant =
                getOwnerRestaurant(identifier);

        if (foodId == null) {

            throw new RuntimeException(
                    "Food ID cannot be null.");
        }

        Food food =
                foodRepository.findById(
                        foodId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Food not found."));

        if (food.getRestaurant() == null ||
                food.getRestaurant().getId() == null ||
                !food.getRestaurant()
                        .getId()
                        .equals(
                                restaurant.getId())) {

            throw new RuntimeException(
                    "You are not allowed to delete this food.");
        }

        foodRepository.delete(food);
    }

    // =========================================================
    // GET OWNER ORDERS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(
            String identifier) {

        Restaurant restaurant =
                getOwnerRestaurant(identifier);

        return orderRepository
                .findByRestaurantId(
                        restaurant.getId())
                .stream()
                .map(this::mapOrder)
                .collect(
                        Collectors.toList());
    }

    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    @Override
    public OrderResponse updateOrderStatus(
            String identifier,
            Long orderId,
            String status) {

        Restaurant restaurant =
                getOwnerRestaurant(identifier);

        if (orderId == null) {

            throw new RuntimeException(
                    "Order ID cannot be null.");
        }

        if (status == null ||
                status.trim().isEmpty()) {

            throw new RuntimeException(
                    "Order status cannot be empty.");
        }

        Order order =
                orderRepository.findById(
                        orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found."));

        // =====================================================
        // SECURITY
        // =====================================================

        if (order.getRestaurantId() == null ||
                !order.getRestaurantId()
                        .equals(
                                restaurant.getId())) {

            throw new RuntimeException(
                    "Unauthorized: This order does not belong to your restaurant.");
        }

        String newStatus =
                normalizeStatus(status);

        // =====================================================
        // VALID STATUS
        // =====================================================

        if (!isValidOrderStatus(
                newStatus)) {

            throw new RuntimeException(
                    "Invalid order status: "
                            + newStatus);
        }

        String currentStatus =
                normalizeStatus(
                        order.getStatus());

        // =====================================================
        // FIX OLD PAID DATA
        // =====================================================

        /*
         * Older orders may have PAID inside the
         * status column. Convert that old value to
         * PENDING when the owner updates the order.
         */

        if ("PAID".equals(currentStatus)) {

            currentStatus =
                    "PENDING";

            order.setStatus(
                    "PENDING");
        }

        // =====================================================
        // TERMINAL STATUS PROTECTION
        // =====================================================

        if ("DELIVERED".equals(currentStatus) &&
                !"DELIVERED".equals(newStatus)) {

            throw new RuntimeException(
                    "Delivered order status cannot be changed.");
        }

        if ("CANCELLED".equals(currentStatus) &&
                !"CANCELLED".equals(newStatus)) {

            throw new RuntimeException(
                    "Cancelled order status cannot be changed.");
        }

        // =====================================================
        // SET STATUS
        // =====================================================

        order.setStatus(
                newStatus);

        return mapOrder(
                orderRepository.save(
                        order));
    }

    // =========================================================
    // VALID ORDER STATUS
    // =========================================================

    private boolean isValidOrderStatus(
            String status) {

        return "PENDING".equals(status)
                || "ACCEPTED".equals(status)
                || "PREPARING".equals(status)
                || "OUT_FOR_DELIVERY".equals(status)
                || "DELIVERED".equals(status)
                || "CANCELLED".equals(status);
    }

    // =========================================================
    // NORMALIZE STATUS
    // =========================================================

    private String normalizeStatus(
            String status) {

        if (status == null ||
                status.trim().isEmpty()) {

            return "PENDING";
        }

        return status
                .trim()
                .toUpperCase()
                .replace(
                        " ",
                        "_")
                .replace(
                        "-",
                        "_");
    }

    // =========================================================
    // PENDING STATUS
    // =========================================================

    private boolean isPendingStatus(
            String status) {

        String normalized =
                normalizeStatus(
                        status);

        return "PENDING".equals(
                    normalized)
                || "ACCEPTED".equals(
                    normalized)
                || "PREPARING".equals(
                    normalized)
                || "OUT_FOR_DELIVERY".equals(
                    normalized);
    }

    // =========================================================
    // DELIVERED / COMPLETED
    // =========================================================

    private boolean isDeliveredStatus(
            String status) {

        String normalized =
                normalizeStatus(
                        status);

        return "DELIVERED".equals(
                    normalized)
                || "COMPLETED".equals(
                    normalized);
    }

    // =========================================================
    // RESTAURANT MAPPER
    // =========================================================

    private RestaurantResponse mapRestaurant(
            Restaurant restaurant) {

        return RestaurantResponse.builder()

                .id(
                        restaurant.getId())

                .restaurantName(
                        restaurant.getRestaurantName())

                .ownerName(
                        restaurant.getOwnerName())

                .email(
                        restaurant.getEmail())

                .phone(
                        restaurant.getPhone())

                .address(
                        restaurant.getAddress())

                .image(
                        restaurant.getImage())

                .description(
                        restaurant.getDescription())

                .openingTime(
                        restaurant.getOpeningTime())

                .closingTime(
                        restaurant.getClosingTime())

                .rating(
                        restaurant.getRating())

                .build();
    }

    // =========================================================
    // FOOD MAPPER
    // =========================================================

    private FoodResponse mapFood(
            Food food) {

        return FoodResponse.builder()

                .id(
                        food.getId())

                .foodName(
                        food.getFoodName())

                .description(
                        food.getDescription())

                .price(
                        food.getPrice())

                .image(
                        food.getImage())

                .category(
                        food.getCategory())

                .available(
                        food.getAvailable())

                .restaurantId(
                        food.getRestaurant() == null
                                ? null
                                : food.getRestaurant()
                                        .getId())

                .restaurantName(
                        food.getRestaurant() == null
                                ? "Not available"
                                : food.getRestaurant()
                                        .getRestaurantName())

                .build();
    }

    // =========================================================
    // ORDER MAPPER
    // =========================================================

    private OrderResponse mapOrder(
            Order order) {

        if (order == null) {
            return null;
        }

        // =====================================================
        // CUSTOMER
        // =====================================================

        String customerName =
                "Not available";

        String customerPhone =
                "Not available";

        if (order.getUserId() != null) {

            User customer =
                    userRepository
                            .findById(
                                    order.getUserId())
                            .orElse(null);

            if (customer != null) {

                if (customer.getFullName() != null &&
                        !customer.getFullName()
                                .trim()
                                .isEmpty()) {

                    customerName =
                            customer.getFullName();
                }

                if (customer.getPhone() != null &&
                        !customer.getPhone()
                                .trim()
                                .isEmpty()) {

                    customerPhone =
                            customer.getPhone();
                }
            }
        }

        // =====================================================
        // RESTAURANT
        // =====================================================

        String restaurantName =
                "Not available";

        if (order.getRestaurantId() != null) {

            Restaurant restaurant =
                    restaurantRepository
                            .findById(
                                    order.getRestaurantId())
                            .orElse(null);

            if (restaurant != null &&
                    restaurant.getRestaurantName() != null &&
                    !restaurant.getRestaurantName()
                            .trim()
                            .isEmpty()) {

                restaurantName =
                        restaurant.getRestaurantName();
            }
        }

        // =====================================================
        // NORMALIZED ORDER STATUS
        // =====================================================

        String orderStatus =
                normalizeStatus(
                        order.getStatus());

        /*
         * Old records may contain PAID in status.
         * Do not expose PAID as delivery status.
         *
         * Until those old rows are manually migrated,
         * treat PAID as PENDING in the response.
         */
        if ("PAID".equals(orderStatus)) {

            orderStatus =
                    "PENDING";
        }

        // =====================================================
        // PAYMENT STATUS
        // =====================================================

        String paymentStatus =
                order.getPaymentStatus();

        if (paymentStatus == null ||
                paymentStatus.trim().isEmpty()) {

            /*
             * Backward compatibility:
             * If old data has PAID in the old status field,
             * expose it as payment status.
             */
            if ("PAID".equalsIgnoreCase(
                    order.getStatus())) {

                paymentStatus =
                        "PAID";

            } else {

                paymentStatus =
                        "PENDING";
            }
        }

        // =====================================================
        // RESPONSE
        // =====================================================

        return OrderResponse.builder()

                .id(
                        order.getId())

                .userId(
                        order.getUserId())

                .restaurantId(
                        order.getRestaurantId())

                .foodId(
                        order.getFoodId())

                .foodName(
                        order.getFoodName())

                .quantity(
                        order.getQuantity())

                .totalAmount(
                        order.getTotalAmount())

                .deliveryAddress(
                        order.getDeliveryAddress())

                .paymentMethod(
                        order.getPaymentMethod())

                .paymentStatus(
                        paymentStatus)

                .status(
                        orderStatus)

                .createdAt(
                        order.getCreatedAt())

                .customerName(
                        customerName)

                .customerPhone(
                        customerPhone)

                .restaurantName(
                        restaurantName)

                .build();
    }
}