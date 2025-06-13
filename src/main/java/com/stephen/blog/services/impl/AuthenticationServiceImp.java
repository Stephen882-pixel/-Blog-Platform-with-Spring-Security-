package com.stephen.blog.services.impl;

import com.stephen.blog.services.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImp implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    // Fixed the property value injection - was @{} instead of ${}
    @Value("${jwt.secret:}")
    private String secretKey;

    private final Long jwtExpiryMs = 86400000L; // 24 hours

    @Override
    public UserDetails authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return userDetailsService.loadUserByUsername(email);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Fixed: was adding expiry to current time instead of setting expiration
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiryMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public UserDetails validateToken(String token) {
        String username = extractUsername(token);
        return userDetailsService.loadUserByUsername(username);
    }

    private String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    private Key getSigningKey() {
        // Check if secret key is configured and valid
        if (secretKey == null || secretKey.trim().isEmpty()) {
            log.warn("JWT secret not configured, generating secure key");
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }

        byte[] keyBytes;
        try {
            // Try to decode as Base64 first
            keyBytes = java.util.Base64.getDecoder().decode(secretKey);
        } catch (IllegalArgumentException e) {
            // If not Base64, use the string directly as bytes
            keyBytes = secretKey.getBytes();
        }

        // Ensure key is at least 256 bits (32 bytes)
        if (keyBytes.length < 32) {
            log.warn("JWT secret is too short ({} bits), generating secure key", keyBytes.length * 8);
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}