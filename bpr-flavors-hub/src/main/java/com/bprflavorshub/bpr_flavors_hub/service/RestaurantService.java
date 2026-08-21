package com.bprflavorshub.bpr_flavors_hub.service;

import java.util.List;

import com.bprflavorshub.bpr_flavors_hub.dto.RestaurantRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.RestaurantResponse;

public interface RestaurantService {

    RestaurantResponse addRestaurant(RestaurantRequest request);

    List<RestaurantResponse> getAllRestaurants();

    RestaurantResponse getRestaurantById(Long id);

    RestaurantResponse updateRestaurant(Long id, RestaurantRequest request);

    void deleteRestaurant(Long id);
}