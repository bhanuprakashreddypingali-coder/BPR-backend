package com.bprflavorshub.bpr_flavors_hub.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret:ThisIsASecretKeyForBPRFlavorsHubJwtAuthentication2026}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    // =========================================================
    // SIGNING KEY
    // =========================================================

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // =========================================================
    // GENERATE TOKEN
    // =========================================================

    public String generateToken(
            String phone) {

        return Jwts.builder()
                .subject(phone)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )
                .signWith(
                        getSigningKey()
                )
                .compact();
    }

    // =========================================================
    // EXTRACT PHONE
    // =========================================================

    public String extractUsername(
            String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    // =========================================================
    // VALIDATE TOKEN
    // =========================================================

    public boolean isTokenValid(
            String token,
            String phone) {

        try {

            String username =
                    extractUsername(token);

            return username != null
                    && username.equals(phone)
                    && !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }

    // =========================================================
    // CHECK EXPIRATION
    // =========================================================

    private boolean isTokenExpired(
            String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // =========================================================
    // EXTRACT CLAIMS
    // =========================================================

    private Claims extractAllClaims(
            String token) {

        return Jwts.parser()
                .verifyWith(
                        getSigningKey()
                )
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}