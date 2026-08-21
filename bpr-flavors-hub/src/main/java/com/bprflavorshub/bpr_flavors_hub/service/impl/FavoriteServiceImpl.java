package com.bprflavorshub.bpr_flavors_hub.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bprflavorshub.bpr_flavors_hub.dto.FavoriteRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.FavoriteResponse;
import com.bprflavorshub.bpr_flavors_hub.entity.Favorite;
import com.bprflavorshub.bpr_flavors_hub.entity.Food;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.FavoriteRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.FoodRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.FavoriteService;

@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    public FavoriteServiceImpl(
            FavoriteRepository favoriteRepository,
            UserRepository userRepository,
            FoodRepository foodRepository) {

        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
    }

    // =====================================================
    // ADD FAVORITE
    // =====================================================

    @Override
    public FavoriteResponse addFavorite(
            String phone,
            FavoriteRequest request) {

        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException(
                    "Authenticated user phone not found");
        }

        if (request == null || request.getFoodId() == null) {
            throw new RuntimeException(
                    "Food ID is required");
        }

        // Find logged-in user using PHONE
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with phone: " + phone));

        // Find food
        Food food = foodRepository.findById(
                request.getFoodId())
                .orElseThrow(() ->
                        new RuntimeException("Food not found"));

        // Check duplicate favorite
        if (favoriteRepository
                .findByUserIdAndFoodId(
                        user.getId(),
                        food.getId())
                .isPresent()) {

            throw new RuntimeException(
                    "Food already added to favorites.");
        }

        // Create favorite
        Favorite favorite = Favorite.builder()
                .userId(user.getId())
                .foodId(food.getId())
                .build();

        Favorite savedFavorite =
                favoriteRepository.save(favorite);

        return mapToResponse(savedFavorite);
    }

    // =====================================================
    // GET MY FAVORITES
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteResponse> getFavoritesByUser(
            String phone) {

        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException(
                    "Authenticated user phone not found");
        }

        // Find logged-in user using PHONE
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with phone: " + phone));

        return favoriteRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =====================================================
    // DELETE MY FAVORITE
    // =====================================================

    @Override
    public void deleteFavorite(
            String phone,
            Long id) {

        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException(
                    "Authenticated user phone not found");
        }

        if (id == null) {
            throw new RuntimeException(
                    "Favorite ID is required");
        }

        // Find logged-in user
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with phone: " + phone));

        // Find favorite
        Favorite favorite =
                favoriteRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Favorite not found"));

        // Security check
        if (!favorite.getUserId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "You are not authorized to delete this favorite");
        }

        favoriteRepository.delete(favorite);
    }

    // =====================================================
    // ENTITY -> DTO
    // =====================================================

    private FavoriteResponse mapToResponse(
            Favorite favorite) {

        return FavoriteResponse.builder()
                .id(favorite.getId())
                .userId(favorite.getUserId())
                .foodId(favorite.getFoodId())
                .build();
    }
}