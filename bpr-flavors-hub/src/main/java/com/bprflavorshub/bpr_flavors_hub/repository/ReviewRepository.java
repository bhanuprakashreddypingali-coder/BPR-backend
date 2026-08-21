package com.bprflavorshub.bpr_flavors_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bprflavorshub.bpr_flavors_hub.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRestaurantId(Long restaurantId);

    List<Review> findByUserId(Long userId);

    Optional<Review> findByRestaurantIdAndUserId(Long restaurantId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.restaurant.id = :restaurantId")
    Double getAverageRating(Long restaurantId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.restaurant.id = :restaurantId")
    Long getReviewCount(Long restaurantId);
}