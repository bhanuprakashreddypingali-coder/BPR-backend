package com.bprflavorshub.bpr_flavors_hub.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bprflavorshub.bpr_flavors_hub.dto.FoodRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.FoodResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.OrderResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.OwnerDashboardResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.RestaurantRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.RestaurantResponse;
import com.bprflavorshub.bpr_flavors_hub.service.OwnerService;
import com.bprflavorshub.bpr_flavors_hub.dto.OwnerReportResponse;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/owner")
@CrossOrigin("*")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    // ==========================
    // Dashboard
    // ==========================

    @GetMapping("/dashboard")
    public ResponseEntity<OwnerDashboardResponse> dashboard(
            Principal principal) {

        return ResponseEntity.ok(
                ownerService.getDashboard(principal.getName()));
    }
    @GetMapping("/reports")
    public ResponseEntity<OwnerReportResponse> getReports(
         Principal principal) {
            
        return ResponseEntity.ok(
            ownerService.getReports(principal.getName()));
    }

    // ==========================
    // Restaurant
    // ==========================

    @GetMapping("/restaurant")
    public ResponseEntity<RestaurantResponse> getRestaurant(
            Principal principal) {

        return ResponseEntity.ok(
                ownerService.getRestaurant(principal.getName()));
    }

    @PutMapping("/restaurant")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            Principal principal,
            @RequestBody RestaurantRequest request) {

        return ResponseEntity.ok(
                ownerService.updateRestaurant(
                        principal.getName(),
                        request));
    }

    // ==========================
    // Foods
    // ==========================

    @GetMapping("/foods")
    public ResponseEntity<List<FoodResponse>> getFoods(
            Principal principal) {

        return ResponseEntity.ok(
                ownerService.getFoods(principal.getName()));
    }

    @PostMapping("/foods")
    public ResponseEntity<FoodResponse> addFood(
            Principal principal,
            @RequestBody FoodRequest request) {

        return ResponseEntity.ok(
                ownerService.addFood(
                        principal.getName(),
                        request));
    }

    @PutMapping("/foods/{id}")
    public ResponseEntity<FoodResponse> updateFood(
            Principal principal,
            @PathVariable Long id,
            @RequestBody FoodRequest request) {

        return ResponseEntity.ok(
                ownerService.updateFood(
                        principal.getName(),
                        id,
                        request));
    }

    @DeleteMapping("/foods/{id}")
    public ResponseEntity<String> deleteFood(
            Principal principal,
            @PathVariable Long id) {

        ownerService.deleteFood(
                principal.getName(),
                id);

        return ResponseEntity.ok("Food deleted successfully.");
    }

    // ==========================
    // Orders
    // ==========================

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(
            Principal principal) {

        return ResponseEntity.ok(
                ownerService.getOrders(principal.getName()));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            Principal principal,
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                ownerService.updateOrderStatus(
                        principal.getName(),
                        id,
                        status));
    }
}