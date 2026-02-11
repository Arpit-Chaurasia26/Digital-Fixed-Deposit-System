package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService implements  ITokenService{

    private static final Logger logger = LogManager.getLogger(TokenService.class);

    private static final long ACCESS_TOKEN_EXPIRY_HOURS = 1;

    private final Key signingKey;

    public TokenService(
            @Value("${security.jwt.secret}") String secret
    ) {
        this.signingKey =
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // Generates ACCESS token (1 hour)
    @Override
    public String generateAccessToken(Long userId, String role) {

        Instant now = Instant.now();
        Instant expiry =
                now.plus(ACCESS_TOKEN_EXPIRY_HOURS, ChronoUnit.HOURS);

        String token = Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        logger.debug("Access token generated for user id={} role={}", userId, role);
        return token;
    }

    // Validates ACCESS token and returns claims. Throws exception if invalid or expired.
    @Override
    public Claims validateAccessToken(String token) {

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            logger.debug("Access token validated");
            return claims;

        } catch (JwtException ex) {
            logger.warn("Access token validation failed");
            throw new UnauthorizedException(
                    "Invalid or expired access token"
            );
        }
    }

    @Override
    public Long extractUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }
    @Override
    public String extractRole(Claims claims) {
        return claims.get("role", String.class);
    }
}
