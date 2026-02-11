package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import io.jsonwebtoken.Claims;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface ITokenService {

    Logger logger = LogManager.getLogger(ITokenService.class);

    // Generate access token
    String generateAccessToken(Long userId, String role);

    // Validate token and return claims
    Claims validateAccessToken(String token);

    // Extract helpers
    Long extractUserId(Claims claims);

    String extractRole(Claims claims);
}
