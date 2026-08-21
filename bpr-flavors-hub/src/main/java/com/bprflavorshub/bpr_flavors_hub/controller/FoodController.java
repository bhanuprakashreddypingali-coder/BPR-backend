package com.bprflavorshub.bpr_flavors_hub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bprflavorshub.bpr_flavors_hub.dto.FoodRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.FoodResponse;
import com.bprflavorshub.bpr_flavors_hub.service.FoodService;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin("*")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @PostMapping
    public ResponseEntity<FoodResponse> addFood(
            @RequestBody FoodRequest request) {

        return ResponseEntity.ok(foodService.addFood(request));
    }

    @GetMapping
    public ResponseEntity<List<FoodResponse>> getAllFoods() {

        return ResponseEntity.ok(foodService.getAllFoods());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFoodById(
            @PathVariable Long id) {

        return ResponseEntity.ok(foodService.getFoodById(id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<FoodResponse>> getFoodsByRestaurant(
            @PathVariable Long restaurantId) {

        return ResponseEntity.ok(
                foodService.getFoodsByRestaurant(restaurantId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodResponse> updateFood(
            @PathVariable Long id,
            @RequestBody FoodRequest request) {

        return ResponseEntity.ok(
                foodService.updateFood(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFood(
            @PathVariable Long id) {

        foodService.deleteFood(id);

        return ResponseEntity.ok("Food deleted successfully.");
    }
}