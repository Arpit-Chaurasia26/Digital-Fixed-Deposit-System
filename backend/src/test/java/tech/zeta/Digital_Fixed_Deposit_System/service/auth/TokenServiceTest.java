package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Priyanshu Mishra
 */

public class TokenServiceTest {

    private static final String SECRET = "VGhpcy1pcy1hLXRlc3Qtc2VjcmV0LWtleS1mb3Itand0";

    @Test
    void generateAndValidateAccessToken_returnsExpectedClaims() {
        TokenService tokenService = new TokenService(SECRET);

        String token = tokenService.generateAccessToken(42L, "USER");
        Claims claims = tokenService.validateAccessToken(token);

        assertEquals("42", claims.getSubject());
        assertEquals("USER", claims.get("role", String.class));
        assertEquals(42L, tokenService.extractUserId(claims));
        assertEquals("USER", tokenService.extractRole(claims));
    }

    @Test
    void validateAccessToken_throwsOnInvalidToken() {
        TokenService tokenService = new TokenService(SECRET);

        assertThrows(UnauthorizedException.class, () -> tokenService.validateAccessToken("invalid-token"));
    }
}
