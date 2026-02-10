package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import io.jsonwebtoken.Claims;

public interface ITokenService {

    // Generate access token
    String generateAccessToken(Long userId, String role);

    // Validate token and return claims
    Claims validateAccessToken(String token);

    // Extract helpers
    Long extractUserId(Claims claims);

    String extractRole(Claims claims);
}
