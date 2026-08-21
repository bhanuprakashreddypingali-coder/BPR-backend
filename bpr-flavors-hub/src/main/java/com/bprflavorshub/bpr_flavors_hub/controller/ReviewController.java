package com.bprflavorshub.bpr_flavors_hub.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bprflavorshub.bpr_flavors_hub.dto.ReviewRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.ReviewResponse;
import com.bprflavorshub.bpr_flavors_hub.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin("*")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(
            Principal principal,
            @RequestBody ReviewRequest request) {

        return ResponseEntity.ok(
                reviewService.addReview(principal.getName(), request));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<ReviewResponse>> getRestaurantReviews(
            @PathVariable Long restaurantId) {

        return ResponseEntity.ok(
                reviewService.getRestaurantReviews(restaurantId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> myReviews(
            Principal principal) {

        return ResponseEntity.ok(
                reviewService.getMyReviews(principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {

        reviewService.deleteReview(id);

        return ResponseEntity.ok("Review deleted.");
    }
}