package com.bprflavorshub.bpr_flavors_hub.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri =
                request.getRequestURI();

        String method =
                request.getMethod();

        System.out.println();
        System.out.println(
                "================================================="
        );

        System.out.println("JWT FILTER");
        System.out.println("METHOD : " + method);
        System.out.println("URI    : " + requestUri);

        // =========================================================
        // GET AUTHORIZATION HEADER
        // =========================================================

        String authHeader =
                request.getHeader("Authorization");

        // =========================================================
        // NO JWT
        // =========================================================

        if (authHeader == null ||
                authHeader.trim().isEmpty()) {

            System.out.println(
                    "JWT : Authorization header NOT FOUND"
            );

            System.out.println(
                    "JWT : Continuing as unauthenticated request"
            );

            System.out.println(
                    "================================================="
            );

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        // =========================================================
        // INVALID HEADER
        // =========================================================

        if (!authHeader.startsWith("Bearer ")) {

            System.out.println(
                    "JWT : Invalid Authorization header format"
            );

            System.out.println(
                    "JWT : Expected Bearer <token>"
            );

            System.out.println(
                    "================================================="
            );

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        // =========================================================
        // EXTRACT TOKEN
        // =========================================================

        String token =
                authHeader.substring(7).trim();

        if (token.isEmpty()) {

            System.out.println(
                    "JWT : Empty token"
            );

            SecurityContextHolder.clearContext();

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        System.out.println(
                "JWT : Bearer token received"
        );

        try {

            // =====================================================
            // EXTRACT PHONE
            // =====================================================

            String phone =
                    jwtService.extractUsername(token);

            System.out.println(
                    "JWT PHONE : " + phone
            );

            if (phone == null ||
                    phone.trim().isEmpty()) {

                System.out.println(
                        "JWT ERROR : Username/phone not found"
                );

                SecurityContextHolder.clearContext();

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            // =====================================================
            // CHECK EXISTING AUTHENTICATION
            // =====================================================

            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() != null) {

                System.out.println(
                        "JWT : SecurityContext already authenticated"
                );

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            // =====================================================
            // LOAD USER
            // =====================================================

            System.out.println(
                    "USER : Loading user by phone..."
            );

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(phone);

            if (userDetails == null) {

                System.out.println(
                        "USER ERROR : UserDetails is NULL"
                );

                SecurityContextHolder.clearContext();

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            System.out.println(
                    "USER FOUND : "
                            + userDetails.getUsername()
            );

            System.out.println(
                    "AUTHORITIES : "
                            + userDetails.getAuthorities()
            );

            // =====================================================
            // VALIDATE TOKEN
            // =====================================================

            boolean valid =
                    jwtService.isTokenValid(
                            token,
                            phone
                    );

            System.out.println(
                    "TOKEN VALID : " + valid
            );

            if (!valid) {

                System.out.println(
                        "JWT ERROR : Token invalid or expired"
                );

                SecurityContextHolder.clearContext();

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            // =====================================================
            // CREATE AUTHENTICATION
            // =====================================================

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            // =====================================================
            // SET SECURITY CONTEXT
            // =====================================================

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            authentication
                    );

            // =====================================================
            // DEBUG INFORMATION
            // =====================================================

            System.out.println(
                    "AUTHENTICATION : SUCCESS"
            );

            System.out.println(
                    "AUTH USER : "
                            + authentication.getName()
            );

            System.out.println(
                    "AUTH AUTHORITIES : "
                            + authentication.getAuthorities()
            );

            System.out.println(
                    "AUTHENTICATED : "
                            + authentication.isAuthenticated()
            );

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "**************** JWT ERROR ****************"
            );

            System.out.println(
                    "REQUEST URI : " + requestUri
            );

            System.out.println(
                    "EXCEPTION : "
                            + e.getClass().getName()
            );

            System.out.println(
                    "MESSAGE : "
                            + e.getMessage()
            );

            System.out.println(
                    "*******************************************"
            );
            System.out.println();

            SecurityContextHolder.clearContext();
        }

        System.out.println(
                "JWT FILTER : Request continuing"
        );

        System.out.println(
                "================================================="
        );

        filterChain.doFilter(
                request,
                response
        );
    }
}