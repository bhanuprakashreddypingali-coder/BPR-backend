package com.bprflavorshub.bpr_flavors_hub.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bprflavorshub.bpr_flavors_hub.dto.RegisterRequest;
import com.bprflavorshub.bpr_flavors_hub.entity.Role;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @Override
    public User register(RegisterRequest request) {

        if (request.getPhone() == null ||
                request.getPhone().trim().isEmpty()) {

            throw new RuntimeException(
                    "Phone number is required");
        }

        String phone = request.getPhone().trim();

        if (userRepository.findByPhone(phone).isPresent()) {

            throw new RuntimeException(
                    "Phone number already exists");
        }

        if (request.getEmail() != null &&
                !request.getEmail().trim().isEmpty() &&
                userRepository.findByEmail(
                        request.getEmail().trim()).isPresent()) {

            throw new RuntimeException(
                    "Email already exists");
        }

        if (request.getPassword() == null ||
                request.getPassword().length() < 6) {

            throw new RuntimeException(
                    "Password must contain at least 6 characters");
        }

        User user = new User();

        user.setFullName(request.getFullName());

        if (request.getEmail() != null &&
                !request.getEmail().trim().isEmpty()) {

            user.setEmail(request.getEmail().trim());
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword())
        );

        user.setPhone(phone);

        user.setAddress(request.getAddress());

        // =====================================================
        // ROLE
        // =====================================================

        Role role = Role.CUSTOMER;

        if (request.getRole() != null &&
                !request.getRole().trim().isEmpty()) {

            try {

                role = Role.valueOf(
                        request.getRole()
                                .trim()
                                .toUpperCase()
                );

            } catch (IllegalArgumentException ex) {

                throw new RuntimeException(
                        "Invalid role. Use CUSTOMER or RESTAURANT_OWNER");
            }
        }

        // Never allow public registration to create ADMIN

        if (role == Role.ADMIN) {

            throw new RuntimeException(
                    "ADMIN account cannot be created through registration");
        }

        user.setRole(role);

        // =====================================================
        // OWNER APPROVAL
        // =====================================================

        if (role == Role.RESTAURANT_OWNER) {

            // Restaurant owner requires admin approval
            user.setApproved(false);

        } else {

            // Customer does not require approval
            user.setApproved(true);
        }

        // =====================================================
        // OTP DISABLED
        // =====================================================

        user.setPhoneVerified(false);

        return userRepository.save(user);
    }

    // =========================================================
    // FIND BY PHONE
    // =========================================================

    @Override
    public User findByPhone(String phone) {

        return userRepository.findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));
    }

    // =========================================================
    // FIND BY EMAIL
    // =========================================================

    @Override
    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));
    }

    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @Override
    public void resetPassword(
            String phone,
            String newPassword) {

        if (phone == null ||
                phone.trim().isEmpty()) {

            throw new RuntimeException(
                    "Phone number is required");
        }

        if (newPassword == null ||
                newPassword.length() < 6) {

            throw new RuntimeException(
                    "Password must contain at least 6 characters");
        }

        User user = userRepository.findByPhone(
                phone.trim()
        ).orElseThrow(() ->
                new RuntimeException(
                        "User not found with this phone number"));

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    // =========================================================
    // APPROVE RESTAURANT OWNER
    // =========================================================

    @Override
    public void approveRestaurantOwner(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + id));

        if (user.getRole() != Role.RESTAURANT_OWNER) {

            throw new RuntimeException(
                    "This user is not a restaurant owner");
        }

        user.setApproved(true);

        userRepository.save(user);
    }

    // =========================================================
    // REJECT RESTAURANT OWNER
    // =========================================================

    @Override
    public void rejectRestaurantOwner(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + id));

        if (user.getRole() != Role.RESTAURANT_OWNER) {

            throw new RuntimeException(
                    "This user is not a restaurant owner");
        }

        user.setApproved(false);

        userRepository.save(user);
    }

    // =========================================================
    // DELETE USER
    // =========================================================

    @Override
    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + id));

        // =====================================================
        // NEVER DELETE ADMIN
        // =====================================================

        if (user.getRole() == Role.ADMIN) {

            throw new RuntimeException(
                    "ADMIN users cannot be deleted");
        }

        // =====================================================
        // DELETE USER
        // =====================================================

        userRepository.delete(user);
    }
}