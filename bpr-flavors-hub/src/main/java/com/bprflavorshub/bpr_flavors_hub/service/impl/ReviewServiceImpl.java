package com.bprflavorshub.bpr_flavors_hub.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bprflavorshub.bpr_flavors_hub.dto.ReviewRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.ReviewResponse;
import com.bprflavorshub.bpr_flavors_hub.entity.Restaurant;
import com.bprflavorshub.bpr_flavors_hub.entity.Review;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.RestaurantRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.ReviewRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.ReviewService;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository) {

        this.reviewRepository = reviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // CUSTOMER ADD REVIEW
    // =========================================================

    @Override
    public ReviewResponse addReview(String identifier, ReviewRequest request) {

        User customer = findCustomer(identifier);

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found."));

        if (request.getRating() == null
                || request.getRating() < 1
                || request.getRating() > 5) {

            throw new RuntimeException("Rating must be between 1 and 5.");
        }

        Review review = reviewRepository
                .findByRestaurantIdAndUserId(
                        restaurant.getId(),
                        customer.getId())
                .orElse(null);

        if (review == null) {

            review = Review.builder()
                    .restaurant(restaurant)
                    .user(customer)
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .createdAt(LocalDateTime.now())
                    .build();

        } else {

            review.setRating(request.getRating());
            review.setComment(request.getComment());
        }

        review = reviewRepository.save(review);

        updateRestaurantAverageRating(restaurant.getId());

        return mapReview(review);
    }

    // =========================================================
    // GET REVIEWS OF RESTAURANT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getRestaurantReviews(Long restaurantId) {

        return reviewRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::mapReview)
                .collect(Collectors.toList());
    }

    // =========================================================
    // CUSTOMER REVIEWS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviews(String identifier) {

        User customer = findCustomer(identifier);

        return reviewRepository.findByUserId(customer.getId())
                .stream()
                .map(this::mapReview)
                .collect(Collectors.toList());
    }

    // =========================================================
    // DELETE REVIEW
    // =========================================================

    @Override
    public void deleteReview(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found."));

        Long restaurantId = review.getRestaurant().getId();

        reviewRepository.delete(review);

        updateRestaurantAverageRating(restaurantId);
    }

    // =========================================================
    // FIND CUSTOMER USING PHONE OR EMAIL
    // =========================================================

    private User findCustomer(String identifier) {

        User user = userRepository.findByPhone(identifier).orElse(null);

        if (user == null) {
            user = userRepository.findByEmail(identifier).orElse(null);
        }

        if (user == null) {
            throw new RuntimeException("Customer not found.");
        }

        return user;
    }

    // =========================================================
    // UPDATE RESTAURANT AVERAGE RATING
    // =========================================================

    private void updateRestaurantAverageRating(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found."));

        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);

        if (reviews.isEmpty()) {

            restaurant.setRating(0.0);

        } else {

            double average = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            restaurant.setRating(Math.round(average * 10.0) / 10.0);
        }

        restaurantRepository.save(restaurant);
    }

    // =========================================================
    // REVIEW RESPONSE MAPPER
    // =========================================================

    private ReviewResponse mapReview(Review review) {

        return ReviewResponse.builder()
                .id(review.getId())
                .restaurantId(review.getRestaurant().getId())
                .restaurantName(review.getRestaurant().getRestaurantName())
                .userId(review.getUser().getId())
                .customerName(review.getUser().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}