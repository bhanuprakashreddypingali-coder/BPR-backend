package com.bprflavorshub.bpr_flavors_hub.service;

import java.util.List;

import com.bprflavorshub.bpr_flavors_hub.dto.FavoriteRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.FavoriteResponse;

public interface FavoriteService {

    // Add favorite for logged-in user
    FavoriteResponse addFavorite(
            String phone,
            FavoriteRequest request);

    // Get favorites of logged-in user
    List<FavoriteResponse> getFavoritesByUser(
            String phone);

    // Delete favorite belonging to logged-in user
    void deleteFavorite(
            String phone,
            Long id);
}