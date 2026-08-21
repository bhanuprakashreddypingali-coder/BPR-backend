package com.bprflavorshub.bpr_flavors_hub.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bprflavorshub.bpr_flavors_hub.dto.CartRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.CartResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.CheckoutRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.CheckoutResponse;

import com.bprflavorshub.bpr_flavors_hub.entity.Cart;
import com.bprflavorshub.bpr_flavors_hub.entity.Food;
import com.bprflavorshub.bpr_flavors_hub.entity.Order;
import com.bprflavorshub.bpr_flavors_hub.entity.User;

import com.bprflavorshub.bpr_flavors_hub.exception.CartEmptyException;

import com.bprflavorshub.bpr_flavors_hub.repository.CartRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.FoodRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.OrderRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;

import com.bprflavorshub.bpr_flavors_hub.service.CartService;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final OrderRepository orderRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            UserRepository userRepository,
            FoodRepository foodRepository,
            OrderRepository orderRepository) {

        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.orderRepository = orderRepository;
    }

    // =========================================================
    // ADD TO CART
    // =========================================================

    @Override
    public CartResponse addToCart(
            String phone,
            CartRequest request) {

        User user =
                getUserByPhone(phone);

        if (request == null ||
                request.getFoodId() == null) {

            throw new RuntimeException(
                    "Food ID is required"
            );
        }

        if (request.getQuantity() == null ||
                request.getQuantity() <= 0) {

            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        Food food =
                foodRepository
                        .findById(
                                request.getFoodId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Food not found with id: "
                                                + request.getFoodId()
                                )
                        );

        if (food.getAvailable() != null &&
                !food.getAvailable()) {

            throw new RuntimeException(
                    "This food is currently unavailable"
            );
        }

        Cart cart =
                cartRepository
                        .findByUserIdAndFoodId(
                                user.getId(),
                                food.getId()
                        )
                        .orElse(
                                new Cart()
                        );

        cart.setUser(user);
        cart.setFood(food);
        cart.setQuantity(
                request.getQuantity()
        );

        cart.setTotalPrice(
                food.getPrice() *
                        request.getQuantity()
        );

        return map(
                cartRepository.save(cart)
        );
    }

    // =========================================================
    // GET CART
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> getCart(
            String phone) {

        User user =
                getUserByPhone(phone);

        return cartRepository
                .findByUserId(
                        user.getId()
                )
                .stream()
                .map(this::map)
                .collect(
                        Collectors.toList()
                );
    }

    // =========================================================
    // UPDATE QUANTITY
    // =========================================================

    @Override
    public CartResponse updateQuantity(
            String phone,
            Long cartId,
            Integer quantity) {

        User user =
                getUserByPhone(phone);

        if (cartId == null) {

            throw new RuntimeException(
                    "Cart ID cannot be null"
            );
        }

        if (quantity == null ||
                quantity <= 0) {

            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Cart item not found"
                                )
                        );

        validateCartOwnership(
                cart,
                user
        );

        if (cart.getFood() == null) {

            throw new RuntimeException(
                    "Food not found for cart item"
            );
        }

        cart.setQuantity(
                quantity
        );

        cart.setTotalPrice(
                cart.getFood().getPrice() *
                        quantity
        );

        return map(
                cartRepository.save(cart)
        );
    }

    // =========================================================
    // REMOVE ITEM
    // =========================================================

    @Override
    public void removeItem(
            String phone,
            Long cartId) {

        User user =
                getUserByPhone(phone);

        if (cartId == null) {

            throw new RuntimeException(
                    "Cart ID cannot be null"
            );
        }

        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Cart item not found"
                                )
                        );

        validateCartOwnership(
                cart,
                user
        );

        cartRepository.delete(cart);
    }

    // =========================================================
    // CLEAR CART
    // =========================================================

    @Override
    public void clearCart(
            String phone) {

        User user =
                getUserByPhone(phone);

        List<Cart> carts =
                cartRepository.findByUserId(
                        user.getId()
                );

        cartRepository.deleteAll(carts);
    }

    // =========================================================
    // CHECKOUT
    // =========================================================

    @Override
    public CheckoutResponse checkout(
            String phone,
            CheckoutRequest request) {

        User user =
                getUserByPhone(phone);

        // -----------------------------------------------------
        // VALIDATE REQUEST
        // -----------------------------------------------------

        if (request == null) {

            throw new RuntimeException(
                    "Checkout request is required"
            );
        }

        if (request.getDeliveryAddress() == null ||
                request.getDeliveryAddress()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "Delivery address is required"
            );
        }

        if (request.getPaymentMethod() == null ||
                request.getPaymentMethod()
                        .trim()
                        .isEmpty()) {

            throw new RuntimeException(
                    "Payment method is required"
            );
        }

        // -----------------------------------------------------
        // GET CART ITEMS
        // -----------------------------------------------------

        List<Cart> cartItems =
                cartRepository.findByUserId(
                        user.getId()
                );

        // -----------------------------------------------------
        // EMPTY CART
        // -----------------------------------------------------

        if (cartItems == null ||
                cartItems.isEmpty()) {

            throw new CartEmptyException(
                    "Cart is empty. Please add items to your cart before checkout."
            );
        }

        List<Long> orderIds =
                new ArrayList<>();

        double grandTotal =
                0.0;

        int totalItems =
                0;

        // -----------------------------------------------------
        // CREATE ONE ORDER FOR EACH CART ITEM
        // -----------------------------------------------------

        for (Cart cart : cartItems) {

            // -------------------------------------------------
            // FOOD VALIDATION
            // -------------------------------------------------

            if (cart.getFood() == null) {

                throw new RuntimeException(
                        "Invalid cart item: food is missing"
                );
            }

            Food food =
                    cart.getFood();

            // -------------------------------------------------
            // RESTAURANT VALIDATION
            // -------------------------------------------------

            if (food.getRestaurant() == null) {

                throw new RuntimeException(
                        "Restaurant is missing for food: "
                                + food.getFoodName()
                );
            }

            // -------------------------------------------------
            // FOOD AVAILABILITY
            // -------------------------------------------------

            if (food.getAvailable() != null &&
                    !food.getAvailable()) {

                throw new RuntimeException(
                        food.getFoodName()
                                + " is currently unavailable"
                );
            }

            // -------------------------------------------------
            // QUANTITY VALIDATION
            // -------------------------------------------------

            Integer quantity =
                    cart.getQuantity();

            if (quantity == null ||
                    quantity <= 0) {

                throw new RuntimeException(
                        "Invalid quantity for "
                                + food.getFoodName()
                );
            }

            // -------------------------------------------------
            // PRICE VALIDATION
            // -------------------------------------------------

            if (food.getPrice() == null) {

                throw new RuntimeException(
                        "Price is missing for food: "
                                + food.getFoodName()
                );
            }

            // -------------------------------------------------
            // CALCULATE ITEM TOTAL
            // -------------------------------------------------

            double itemTotal =
                    food.getPrice() *
                            quantity;

            // -------------------------------------------------
            // CREATE ORDER
            // -------------------------------------------------

            Order order =
                    Order.builder()

                            .userId(
                                    user.getId()
                            )

                            .restaurantId(
                                    food.getRestaurant()
                                            .getId()
                            )

                            .foodId(
                                    food.getId()
                            )

                            .foodName(
                                    food.getFoodName()
                            )

                            .quantity(
                                    quantity
                            )

                            .totalAmount(
                                    itemTotal
                            )

                            .deliveryAddress(
                                    request.getDeliveryAddress()
                            )

                            .paymentMethod(
                                    request.getPaymentMethod()
                            )

                            // =================================
                            // IMPORTANT
                            // =================================
                            // Delivery/order status
                            .status(
                                    "PENDING"
                            )

                            // Payment status
                            .paymentStatus(
                                    "PENDING"
                            )

                            .build();

            // -------------------------------------------------
            // SAVE ORDER
            // -------------------------------------------------

            Order savedOrder =
                    orderRepository.save(
                            order
                    );

            orderIds.add(
                    savedOrder.getId()
            );

            grandTotal +=
                    itemTotal;

            totalItems +=
                    quantity;
        }

        // -----------------------------------------------------
        // CLEAR CART AFTER ORDERS ARE CREATED
        // -----------------------------------------------------

        cartRepository.deleteAll(
                cartItems
        );

        // -----------------------------------------------------
        // RETURN CHECKOUT RESPONSE
        // -----------------------------------------------------

        return CheckoutResponse.builder()

                .message(
                        "Checkout completed successfully."
                )

                .totalItems(
                        totalItems
                )

                .totalAmount(
                        grandTotal
                )

                .orderIds(
                        orderIds
                )

                .build();
    }

    // =========================================================
    // FIND USER BY PHONE
    // =========================================================

    private User getUserByPhone(
            String phone) {

        if (phone == null ||
                phone.trim().isEmpty()) {

            throw new RuntimeException(
                    "Authenticated phone number is missing"
            );
        }

        return userRepository
                .findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with phone: "
                                        + phone
                        )
                );
    }

    // =========================================================
    // VALIDATE CART OWNERSHIP
    // =========================================================

    private void validateCartOwnership(
            Cart cart,
            User user) {

        if (cart.getUser() == null ||
                cart.getUser().getId() == null ||
                !cart.getUser()
                        .getId()
                        .equals(
                                user.getId()
                        )) {

            throw new RuntimeException(
                    "Unauthorized cart access"
            );
        }
    }

    // =========================================================
    // MAP CART -> RESPONSE
    // =========================================================

    private CartResponse map(
            Cart cart) {

        if (cart == null ||
                cart.getFood() == null) {

            throw new RuntimeException(
                    "Invalid cart item"
            );
        }

        return CartResponse.builder()

                .id(
                        cart.getId()
                )

                .foodId(
                        cart.getFood().getId()
                )

                .foodName(
                        cart.getFood().getFoodName()
                )

                .price(
                        cart.getFood().getPrice()
                )

                .quantity(
                        cart.getQuantity()
                )

                .total(
                        cart.getTotalPrice()
                )

                .build();
    }
}