package com.example.bankcards.config;

public interface JwtService {
    String generateToken(String username, String role, Long userId);
    boolean validateToken(String token);
    String extractUsername(String token);
    String extractRole(String token);
    Long extractUserId(String token);
}