package com.bprflavorshub.bpr_flavors_hub.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bprflavorshub.bpr_flavors_hub.dto.UserResponse;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public UserResponse getProfile(Authentication authentication) {

        System.out.println("\n========== USER PROFILE ==========");

        if (authentication == null) {
            System.out.println("Authentication is NULL");
        } else {
            System.out.println("Authentication : " + authentication);
            System.out.println("Username : " + authentication.getName());

            authentication.getAuthorities().forEach(authority ->
                    System.out.println("Authority : " + authority.getAuthority()));
        }

        System.out.println("==================================");

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole().name())
                .build();
    }
}