package com.bprflavorshub.bpr_flavors_hub.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bprflavorshub.bpr_flavors_hub.entity.Role;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.service.UserService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // =========================================================
    // ADMIN DASHBOARD
    // GET /api/admin/dashboard
    // =========================================================

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {

        try {

            List<User> users = userService.getAllUsers();

            long totalUsers = users.size();

            long totalRestaurants = users.stream()
                    .filter(user ->
                            user.getRole() == Role.RESTAURANT_OWNER)
                    .count();

            long pendingOwners = users.stream()
                    .filter(user ->
                            user.getRole() == Role.RESTAURANT_OWNER
                            && !user.isApproved())
                    .count();

            long approvedOwners = users.stream()
                    .filter(user ->
                            user.getRole() == Role.RESTAURANT_OWNER
                            && user.isApproved())
                    .count();

            Map<String, Object> dashboard = new HashMap<>();

            dashboard.put("totalUsers", totalUsers);
            dashboard.put("totalRestaurants", totalRestaurants);
            dashboard.put("totalFoods", 0);
            dashboard.put("totalOrders", 0);
            dashboard.put("totalRevenue", 0);
            dashboard.put("pendingOwners", pendingOwners);
            dashboard.put("approvedOwners", approvedOwners);

            return ResponseEntity.ok(dashboard);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // GET PENDING RESTAURANT OWNERS
    // GET /api/admin/owners/pending
    // =========================================================

    @GetMapping("/owners/pending")
    public ResponseEntity<?> getPendingOwners() {

        try {

            List<User> pendingOwners =
                    userService.getAllUsers()
                            .stream()
                            .filter(user ->
                                    user.getRole()
                                            == Role.RESTAURANT_OWNER)
                            .filter(user ->
                                    !user.isApproved())
                            .collect(Collectors.toList());

            return ResponseEntity.ok(pendingOwners);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // GET ALL USERS
    // GET /api/admin/users
    // =========================================================

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {

        try {

            List<User> users =
                    userService.getAllUsers();

            return ResponseEntity.ok(users);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // APPROVE RESTAURANT OWNER
    // PUT /api/admin/owners/{id}/approve
    // =========================================================

    @PutMapping("/owners/{id}/approve")
    public ResponseEntity<?> approveOwner(
            @PathVariable Long id) {

        try {

            userService.approveRestaurantOwner(id);

            return ResponseEntity.ok(
                    "Restaurant owner approved successfully."
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // REJECT RESTAURANT OWNER
    // PUT /api/admin/owners/{id}/reject
    // =========================================================

    @PutMapping("/owners/{id}/reject")
    public ResponseEntity<?> rejectOwner(
            @PathVariable Long id) {

        try {

            userService.rejectRestaurantOwner(id);

            return ResponseEntity.ok(
                    "Restaurant owner rejected successfully."
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // DELETE USER
    // DELETE /api/admin/users/{id}
    // =========================================================

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id) {

        try {

            userService.deleteUser(id);

            return ResponseEntity.ok(
                    "User deleted successfully."
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Unable to delete user. "
                            + "The user may have related records."
                    );
        }
    }
}