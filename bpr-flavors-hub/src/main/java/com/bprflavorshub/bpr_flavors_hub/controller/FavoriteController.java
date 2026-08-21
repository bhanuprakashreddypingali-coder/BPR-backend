package com.bprflavorshub.bpr_flavors_hub.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bprflavorshub.bpr_flavors_hub.dto.FavoriteRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.FavoriteResponse;
import com.bprflavorshub.bpr_flavors_hub.service.FavoriteService;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin("*")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(
            FavoriteService favoriteService) {

        this.favoriteService = favoriteService;
    }

    // =====================================================
    // ADD FAVORITE
    // =====================================================

    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(
            Principal principal,
            @RequestBody FavoriteRequest request) {

        return ResponseEntity.ok(
                favoriteService.addFavorite(
                        principal.getName(),
                        request));
    }

    // =====================================================
    // GET MY FAVORITES
    // =====================================================

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getMyFavorites(
            Principal principal) {

        return ResponseEntity.ok(
                favoriteService.getFavoritesByUser(
                        principal.getName()));
    }

    // =====================================================
    // DELETE MY FAVORITE
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFavorite(
            Principal principal,
            @PathVariable Long id) {

        favoriteService.deleteFavorite(
                principal.getName(),
                id);

        return ResponseEntity.ok(
                "Favorite deleted successfully.");
    }
}