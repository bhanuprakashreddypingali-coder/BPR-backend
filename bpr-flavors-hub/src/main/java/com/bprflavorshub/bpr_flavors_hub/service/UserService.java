package com.bprflavorshub.bpr_flavors_hub.service;

import java.util.List;

import com.bprflavorshub.bpr_flavors_hub.dto.RegisterRequest;
import com.bprflavorshub.bpr_flavors_hub.entity.User;

public interface UserService {

    // Register user
    User register(RegisterRequest request);

    // Find user
    User findByPhone(String phone);

    User findByEmail(String email);

    // Reset password
    void resetPassword(String phone, String newPassword);

    // Admin user management
    List<User> getAllUsers();

    void approveRestaurantOwner(Long id);

    void rejectRestaurantOwner(Long id);

    void deleteUser(Long id);
}