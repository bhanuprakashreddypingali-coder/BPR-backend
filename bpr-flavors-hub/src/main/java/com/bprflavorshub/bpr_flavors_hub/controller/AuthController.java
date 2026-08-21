package com.bprflavorshub.bpr_flavors_hub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bprflavorshub.bpr_flavors_hub.dto.LoginRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.LoginResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.RegisterRequest;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.security.JwtService;
import com.bprflavorshub.bpr_flavors_hub.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService) {

        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {

            User user = userService.register(request);

            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // LOGIN
    // PHONE + PASSWORD
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        System.out.println();
        System.out.println("======================================");
        System.out.println("LOGIN REQUEST");
        System.out.println("PHONE : " + request.getPhone());
        System.out.println("======================================");

        try {

            // -------------------------------------------------
            // VALIDATE REQUEST
            // -------------------------------------------------

            if (request.getPhone() == null ||
                    request.getPhone().trim().isEmpty()) {

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Phone number is required.");
            }

            if (request.getPassword() == null ||
                    request.getPassword().trim().isEmpty()) {

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Password is required.");
            }

            String phone = request.getPhone().trim();

            // -------------------------------------------------
            // AUTHENTICATE
            //
            // IMPORTANT:
            // CustomUserDetailsService must load by PHONE.
            // -------------------------------------------------

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    phone,
                                    request.getPassword()
                            )
                    );

            System.out.println(
                    "AUTHENTICATION SUCCESS : "
                            + authentication.isAuthenticated()
            );

            // -------------------------------------------------
            // LOAD USER BY PHONE
            // -------------------------------------------------

            User user = userRepository
                    .findByPhone(phone)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "User not found with phone: "
                                            + phone
                            )
                    );

            System.out.println(
                    "USER FOUND : "
                            + user.getFullName()
            );

            System.out.println(
                    "USER PHONE : "
                            + user.getPhone()
            );

            System.out.println(
                    "USER ROLE : "
                            + user.getRole()
            );

            // -------------------------------------------------
            // RESTAURANT OWNER APPROVAL
            // -------------------------------------------------

            if (user.getRole() != null &&
                    user.getRole().name()
                            .equalsIgnoreCase(
                                    "RESTAURANT_OWNER")) {

                Boolean approved = user.getApproved();

                if (approved == null ||
                        !approved) {

                    System.out.println(
                            "OWNER LOGIN BLOCKED - NOT APPROVED"
                    );

                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .body(
                                    "Your restaurant owner account is waiting for admin approval."
                            );
                }
            }

            // -------------------------------------------------
            // GENERATE JWT
            //
            // PHONE IS THE JWT SUBJECT
            // -------------------------------------------------

            String token =
                    jwtService.generateToken(
                            user.getPhone()
                    );

            System.out.println(
                    "JWT GENERATED SUCCESSFULLY"
            );

            // -------------------------------------------------
            // CREATE RESPONSE
            //
            // NO LoginResponse.builder()
            // -------------------------------------------------

            LoginResponse response =
                    new LoginResponse();

            response.setToken(token);
            response.setType("Bearer");
            response.setId(user.getId());
            response.setFullName(user.getFullName());
            response.setEmail(user.getEmail());
            response.setPhone(user.getPhone());
            response.setAddress(user.getAddress());

            if (user.getRole() != null) {

                response.setRole(
                        user.getRole()
                                .name()
                                .toUpperCase()
                );
            }

            response.setApproved(
                    user.getApproved()
            );

            System.out.println();
            System.out.println("======================================");
            System.out.println("LOGIN SUCCESS");
            System.out.println("PHONE : " + user.getPhone());
            System.out.println("ROLE  : " + response.getRole());
            System.out.println("TOKEN : GENERATED");
            System.out.println("======================================");

            return ResponseEntity.ok(response);

        } catch (org.springframework.security.authentication.BadCredentialsException e) {

            System.out.println();
            System.out.println("======================================");
            System.out.println("LOGIN FAILED");
            System.out.println("INVALID PHONE OR PASSWORD");
            System.out.println("======================================");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "Invalid phone number or password."
                    );

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Login failed."
                    );
        }
    }
}