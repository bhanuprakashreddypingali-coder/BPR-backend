package com.bprflavorshub.bpr_flavors_hub.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserDetailsService userDetailsService) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    // =========================================================
    // AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

                // =====================================================
                // CSRF
                // =====================================================

                .csrf(csrf -> csrf.disable())

                // =====================================================
                // CORS
                // =====================================================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // =====================================================
                // SESSION
                // =====================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =====================================================
                // AUTHENTICATION PROVIDER
                // =====================================================

                .authenticationProvider(
                        authenticationProvider()
                )

                // =====================================================
                // AUTHORIZATION
                // =====================================================

                .authorizeHttpRequests(auth -> auth

                        // =================================================
                        // OPTIONS / CORS PREFLIGHT
                        // =================================================

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // =================================================
                        // AUTHENTICATION
                        // =================================================

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // =================================================
                        // PUBLIC RESTAURANTS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/restaurants",
                                "/api/restaurants/**"
                        ).permitAll()

                        // =================================================
                        // PUBLIC FOODS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/foods",
                                "/api/foods/**"
                        ).permitAll()

                        // =================================================
                        // SUPPORT - ADMIN
                        //
                        // Admin can:
                        // - View all tickets
                        // - View ticket details
                        // - Reply
                        // - Change status
                        // - Resolve
                        // - Close
                        // =================================================

                        .requestMatchers(
                                "/api/admin/support/**"
                        ).hasRole("ADMIN")

                        // =================================================
                        // SUPPORT - CUSTOMER + RESTAURANT OWNER
                        //
                        // Customers and restaurant owners can:
                        // - Create tickets
                        // - View their own tickets
                        // - Reply to their tickets
                        // =================================================

                        .requestMatchers(
                                "/api/support/**"
                        ).hasAnyRole(
                                "CUSTOMER",
                                "RESTAURANT_OWNER"
                        )

                        // =================================================
                        // ADMIN
                        // =================================================

                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // =================================================
                        // RESTAURANT OWNER
                        // =================================================

                        .requestMatchers(
                                "/api/owner/**"
                        ).hasRole("RESTAURANT_OWNER")

                        // =================================================
                        // WISHLIST
                        // =================================================

                        .requestMatchers(
                                "/api/wishlist",
                                "/api/wishlist/**"
                        ).authenticated()

                        // =================================================
                        // CART
                        // =================================================

                        .requestMatchers(
                                "/api/cart",
                                "/api/cart/**"
                        ).authenticated()

                        // =================================================
                        // ORDERS
                        // =================================================

                        .requestMatchers(
                                "/api/orders",
                                "/api/orders/**"
                        ).authenticated()

                        // =================================================
                        // FAVORITES
                        // =================================================

                        .requestMatchers(
                                "/api/favorites",
                                "/api/favorites/**"
                        ).authenticated()

                        // =================================================
                        // REVIEWS
                        // =================================================

                        .requestMatchers(
                                "/api/reviews",
                                "/api/reviews/**"
                        ).authenticated()

                        // =================================================
                        // ADDRESSES
                        // =================================================

                        .requestMatchers(
                                "/api/addresses",
                                "/api/addresses/**"
                        ).authenticated()

                        // =================================================
                        // USERS
                        // =================================================

                        .requestMatchers(
                                "/api/users",
                                "/api/users/**"
                        ).authenticated()

                        // =================================================
                        // RESTAURANT WRITE OPERATIONS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/restaurants/**"
                        ).hasRole("RESTAURANT_OWNER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/restaurants/**"
                        ).hasRole("RESTAURANT_OWNER")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/restaurants/**"
                        ).hasRole("RESTAURANT_OWNER")

                        // =================================================
                        // FOOD WRITE OPERATIONS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/foods/**"
                        ).hasRole("RESTAURANT_OWNER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/foods/**"
                        ).hasRole("RESTAURANT_OWNER")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/foods/**"
                        ).hasRole("RESTAURANT_OWNER")

                        // =================================================
                        // EVERYTHING ELSE
                        // =================================================

                        .anyRequest().authenticated()
                )

                // =====================================================
                // JWT FILTER
                // =====================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // =========================================================
    // CORS CONFIGURATION
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.asList(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://localhost:3000"
                )
        );

        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                Arrays.asList(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin",
                        "X-Requested-With"
                )
        );

        configuration.setExposedHeaders(
                Arrays.asList(
                        "Authorization"
                )
        );

        configuration.setAllowCredentials(false);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}