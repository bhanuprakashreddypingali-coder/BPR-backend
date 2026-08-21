package com.bprflavorshub.bpr_flavors_hub.service;

import java.util.List;

import com.bprflavorshub.bpr_flavors_hub.dto.ReviewRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.ReviewResponse;

public interface ReviewService {

    ReviewResponse addReview(String phone, ReviewRequest request);

    List<ReviewResponse> getRestaurantReviews(Long restaurantId);

    List<ReviewResponse> getMyReviews(String phone);

    void deleteReview(Long reviewId);
}