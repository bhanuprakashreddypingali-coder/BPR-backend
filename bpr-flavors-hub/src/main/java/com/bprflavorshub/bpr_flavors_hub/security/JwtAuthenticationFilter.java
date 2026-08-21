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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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
        System.out.println("-------------------------------------------------");
        System.out.println("JWT FILTER");
        System.out.println("Method : " + method);
        System.out.println("URI    : " + requestUri);

        // =========================================================
        // GET AUTHORIZATION HEADER
        // =========================================================

        String authHeader =
                request.getHeader("Authorization");

        // =========================================================
        // NO AUTHORIZATION HEADER
        // =========================================================

        if (authHeader == null ||
                authHeader.trim().isEmpty()) {

            System.out.println(
                    "JWT STATUS : Authorization header is MISSING"
            );

            System.out.println(
                    "JWT RESULT : Request continues WITHOUT authentication"
            );

            System.out.println("-------------------------------------------------");

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        // =========================================================
        // WRONG AUTHORIZATION FORMAT
        // =========================================================

        if (!authHeader.startsWith("Bearer ")) {

            System.out.println(
                    "JWT STATUS : Authorization header exists"
            );

            System.out.println(
                    "JWT ERROR  : Header does NOT start with 'Bearer '"
            );

            System.out.println(
                    "JWT HEADER : " + authHeader
            );

            System.out.println("-------------------------------------------------");

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
                    "JWT ERROR : Bearer token is EMPTY"
            );

            System.out.println("-------------------------------------------------");

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        System.out.println(
                "JWT STATUS : Bearer token received"
        );

        try {

            // =====================================================
            // EXTRACT PHONE
            // =====================================================

            String phone =
                    jwtService.extractUsername(token);

            System.out.println(
                    "JWT PHONE  : " + phone
            );

            if (phone == null ||
                    phone.trim().isEmpty()) {

                System.out.println(
                        "JWT ERROR  : Could not extract phone from token"
                );

                SecurityContextHolder.clearContext();

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            // =====================================================
            // ALREADY AUTHENTICATED
            // =====================================================

            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() != null) {

                System.out.println(
                        "JWT STATUS : SecurityContext already contains authentication"
                );

                System.out.println(
                        "AUTH USER  : "
                                + SecurityContextHolder
                                        .getContext()
                                        .getAuthentication()
                                        .getName()
                );

                System.out.println("-------------------------------------------------");

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
                    "USER LOAD   : Loading user by phone..."
            );

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(phone);

            if (userDetails == null) {

                System.out.println(
                        "USER ERROR  : UserDetails is NULL"
                );

                SecurityContextHolder.clearContext();

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            System.out.println(
                    "USER FOUND  : "
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
                        "JWT ERROR   : Token is INVALID or EXPIRED"
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

            System.out.println(
                    "AUTHENTICATION : SUCCESS"
            );

            System.out.println(
                    "AUTH USER      : "
                            + authentication.getName()
            );

            System.out.println(
                    "AUTH AUTHORITIES: "
                            + authentication.getAuthorities()
            );

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "**************** JWT ERROR ****************"
            );

            System.out.println(
                    "Request URI : " + requestUri
            );

            System.out.println(
                    "Exception   : "
                            + e.getClass().getName()
            );

            System.out.println(
                    "Message     : "
                            + e.getMessage()
            );

            System.out.println(
                    "*******************************************"
            );
            System.out.println();

            SecurityContextHolder.clearContext();
        }

        System.out.println(
                "JWT FILTER : Continuing request"
        );

        System.out.println("-------------------------------------------------");

        filterChain.doFilter(
                request,
                response
        );
    }
}