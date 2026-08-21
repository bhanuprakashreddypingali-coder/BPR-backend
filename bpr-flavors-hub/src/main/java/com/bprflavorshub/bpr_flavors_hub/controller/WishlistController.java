package com.bprflavorshub.bpr_flavors_hub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bprflavorshub.bpr_flavors_hub.dto.WishlistResponse;
import com.bprflavorshub.bpr_flavors_hub.service.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin("*")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(
            WishlistService wishlistService) {

        this.wishlistService = wishlistService;
    }

    // =========================================================
    // ADD TO WISHLIST
    // =========================================================

    @PostMapping
    public ResponseEntity<String> addWishlist(
            @RequestParam Long foodId,
            Authentication authentication) {

        String phone = authentication.getName();

        String message =
                wishlistService.addWishlist(
                        phone,
                        foodId
                );

        return ResponseEntity.ok(message);
    }

    // =========================================================
    // GET MY WISHLIST
    // =========================================================

    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getWishlist(
            Authentication authentication) {

        String phone = authentication.getName();

        List<WishlistResponse> wishlist =
                wishlistService.getWishlist(phone);

        return ResponseEntity.ok(wishlist);
    }

    // =========================================================
    // REMOVE FROM WISHLIST
    // =========================================================

    @DeleteMapping
    public ResponseEntity<String> removeWishlist(
            @RequestParam Long foodId,
            Authentication authentication) {

        String phone = authentication.getName();

        String message =
                wishlistService.removeWishlist(
                        phone,
                        foodId
                );

        return ResponseEntity.ok(message);
    }
}