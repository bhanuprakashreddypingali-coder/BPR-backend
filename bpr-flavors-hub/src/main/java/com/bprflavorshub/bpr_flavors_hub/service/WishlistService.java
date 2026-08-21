package com.bprflavorshub.bpr_flavors_hub.service;

import java.util.List;

import com.bprflavorshub.bpr_flavors_hub.dto.WishlistResponse;

public interface WishlistService {

    String addWishlist(String phone, Long foodId);

    List<WishlistResponse> getWishlist(String phone);

    String removeWishlist(String phone, Long foodId);
}