package com.bprflavorshub.bpr_flavors_hub.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bprflavorshub.bpr_flavors_hub.dto.WishlistResponse;
import com.bprflavorshub.bpr_flavors_hub.entity.Food;
import com.bprflavorshub.bpr_flavors_hub.entity.Restaurant;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.entity.Wishlist;
import com.bprflavorshub.bpr_flavors_hub.repository.FoodRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.WishlistRepository;
import com.bprflavorshub.bpr_flavors_hub.service.WishlistService;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    public WishlistServiceImpl(
            WishlistRepository wishlistRepository,
            UserRepository userRepository,
            FoodRepository foodRepository) {

        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
    }

    // =========================================================
    // ADD TO WISHLIST
    // =========================================================

    @Override
    public String addWishlist(
            String phone,
            Long foodId) {

        if (foodId == null) {
            return "Food ID is required";
        }

        User user = userRepository
                .findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        Food food = foodRepository
                .findById(foodId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Food not found"
                        )
                );

        boolean alreadyExists =
                wishlistRepository
                        .findByUserIdAndFoodId(
                                user.getId(),
                                food.getId()
                        )
                        .isPresent();

        if (alreadyExists) {
            return "Food is already in wishlist";
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .food(food)
                .build();

        wishlistRepository.save(wishlist);

        return "Food added to wishlist successfully";
    }

    // =========================================================
    // GET MY WISHLIST
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponse> getWishlist(
            String phone) {

        User user = userRepository
                .findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        List<Wishlist> wishlistList =
                wishlistRepository
                        .findByUserId(user.getId());

        return wishlistList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // CONVERT ENTITY TO DTO
    // =========================================================

    private WishlistResponse convertToResponse(
            Wishlist wishlist) {

        Food food = wishlist.getFood();

        Restaurant restaurant =
                food != null
                        ? food.getRestaurant()
                        : null;

        return WishlistResponse.builder()

                .wishlistId(
                        wishlist.getId()
                )

                .foodId(
                        food != null
                                ? food.getId()
                                : null
                )

                .foodName(
                        food != null
                                ? food.getFoodName()
                                : null
                )

                .description(
                        food != null
                                ? food.getDescription()
                                : null
                )

                .price(
                        food != null
                                ? food.getPrice()
                                : null
                )

                .image(
                        food != null
                                ? food.getImage()
                                : null
                )

                .category(
                        food != null
                                ? food.getCategory()
                                : null
                )

                .available(
                        food != null
                                ? food.getAvailable()
                                : null
                )

                .restaurantId(
                        restaurant != null
                                ? restaurant.getId()
                                : null
                )

                .restaurantName(
                        restaurant != null
                                ? restaurant.getRestaurantName()
                                : null
                )

                .restaurantImage(
                        restaurant != null
                                ? restaurant.getImage()
                                : null
                )

                .restaurantRating(
                        restaurant != null
                                ? restaurant.getRating()
                                : null
                )

                .build();
    }

    // =========================================================
    // REMOVE FROM WISHLIST
    // =========================================================

    @Override
    public String removeWishlist(
            String phone,
            Long foodId) {

        if (foodId == null) {
            return "Food ID is required";
        }

        User user = userRepository
                .findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        Food food = foodRepository
                .findById(foodId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Food not found"
                        )
                );

        boolean exists =
                wishlistRepository
                        .findByUserIdAndFoodId(
                                user.getId(),
                                food.getId()
                        )
                        .isPresent();

        if (!exists) {
            return "Food is not in wishlist";
        }

        wishlistRepository.deleteByUserIdAndFoodId(
                user.getId(),
                food.getId()
        );

        return "Food removed from wishlist successfully";
    }
}