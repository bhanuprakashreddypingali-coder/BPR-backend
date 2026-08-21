package com.bprflavorshub.bpr_flavors_hub.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bprflavorshub.bpr_flavors_hub.dto.RestaurantRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.RestaurantResponse;
import com.bprflavorshub.bpr_flavors_hub.entity.Restaurant;
import com.bprflavorshub.bpr_flavors_hub.entity.Review;
import com.bprflavorshub.bpr_flavors_hub.entity.Role;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.RestaurantRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.ReviewRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.RestaurantService;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public RestaurantServiceImpl(
            RestaurantRepository restaurantRepository,
            UserRepository userRepository,
            ReviewRepository reviewRepository) {

        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    // ============================================================
    // ADD RESTAURANT
    // ============================================================

    @Override
    public RestaurantResponse addRestaurant(RestaurantRequest request) {

        User owner = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() ->
                        new RuntimeException("Restaurant owner not found."));

        if (owner.getRole() != Role.RESTAURANT_OWNER) {
            throw new RuntimeException("User is not a restaurant owner.");
        }

        Restaurant restaurant = Restaurant.builder()
                .restaurantName(request.getRestaurantName())
                .ownerName(request.getOwnerName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .image(request.getImage())
                .description(request.getDescription())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .rating(0.0)
                .owner(owner)
                .build();

        restaurant = restaurantRepository.save(restaurant);

        return mapToResponse(restaurant);
    }

    // ============================================================
    // GET ALL RESTAURANTS
    // ============================================================

    @Override
    public List<RestaurantResponse> getAllRestaurants() {

        return restaurantRepository.findAll()
                .stream()
                .map(restaurant -> {

                    Double rating = calculateRestaurantRating(restaurant.getId());

                    restaurant.setRating(rating);

                    restaurantRepository.save(restaurant);

                    return mapToResponse(restaurant);
                })
                .collect(Collectors.toList());
    }

    // ============================================================
    // GET RESTAURANT BY ID
    // ============================================================

    @Override
    public RestaurantResponse getRestaurantById(Long id) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found."));

        Double rating = calculateRestaurantRating(id);

        restaurant.setRating(rating);

        restaurantRepository.save(restaurant);

        return mapToResponse(restaurant);
    }

    // ============================================================
    // UPDATE RESTAURANT
    // ============================================================

    @Override
    public RestaurantResponse updateRestaurant(
            Long id,
            RestaurantRequest request) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found."));

        restaurant.setRestaurantName(request.getRestaurantName());
        restaurant.setOwnerName(request.getOwnerName());
        restaurant.setEmail(request.getEmail());
        restaurant.setPhone(request.getPhone());
        restaurant.setAddress(request.getAddress());
        restaurant.setImage(request.getImage());
        restaurant.setDescription(request.getDescription());
        restaurant.setOpeningTime(request.getOpeningTime());
        restaurant.setClosingTime(request.getClosingTime());

        restaurant = restaurantRepository.save(restaurant);

        return mapToResponse(restaurant);
    }

    // ============================================================
    // DELETE RESTAURANT
    // ============================================================

    @Override
    public void deleteRestaurant(Long id) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Restaurant not found."));

        restaurantRepository.delete(restaurant);
    }

    // ============================================================
    // CALCULATE RESTAURANT RATING
    // ============================================================

    private Double calculateRestaurantRating(Long restaurantId) {

        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);

        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return Math.round(average * 10.0) / 10.0;
    }

    // ============================================================
    // MAPPER
    // ============================================================

    private RestaurantResponse mapToResponse(Restaurant restaurant) {

        Double rating = restaurant.getRating();

        if (rating == null) {
            rating = 0.0;
        }

        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .restaurantName(restaurant.getRestaurantName())
                .ownerName(restaurant.getOwnerName())
                .email(restaurant.getEmail())
                .phone(restaurant.getPhone())
                .address(restaurant.getAddress())
                .image(restaurant.getImage())
                .description(restaurant.getDescription())
                .openingTime(restaurant.getOpeningTime())
                .closingTime(restaurant.getClosingTime())
                .rating(rating)
                .build();
    }
}