package com.bprflavorshub.bpr_flavors_hub.service;

import java.util.List;

import com.bprflavorshub.bpr_flavors_hub.dto.FoodRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.FoodResponse;

public interface FoodService {

    FoodResponse addFood(FoodRequest request);

    List<FoodResponse> getAllFoods();

    FoodResponse getFoodById(Long id);

    List<FoodResponse> getFoodsByRestaurant(Long restaurantId);

    FoodResponse updateFood(Long id, FoodRequest request);

    void deleteFood(Long id);

}