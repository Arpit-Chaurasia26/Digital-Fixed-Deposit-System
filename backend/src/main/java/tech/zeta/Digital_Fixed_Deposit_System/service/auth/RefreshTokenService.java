package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.RefreshToken;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.RefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * @author Priyanshu Mishra
 */

@Slf4j
@Service
public class RefreshTokenService{

    private static final long REFRESH_TOKEN_DAYS = 7;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public RefreshToken createRefreshToken(Long userId) {
       String tokenValue= UUID.randomUUID().toString();
        Instant creationOfToken=Instant.now();

        Instant expiryOfToken= Instant.now().plus(REFRESH_TOKEN_DAYS, ChronoUnit.DAYS);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(tokenValue)
                .expiresAt(expiryOfToken)
                .createdAt(creationOfToken)
                .revoked(false)
                .build();
       RefreshToken saved = refreshTokenRepository.save(refreshToken);
       log.info("Refresh token created for user id={}", userId);
       return saved;
    }

    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new UnauthorizedException("Invalid refresh token")
                        );

        if (refreshToken.isRevoked()) {
            log.warn("Refresh token revoked");
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            log.warn("Refresh token expired");
            throw new UnauthorizedException("Refresh token has expired");
        }

        log.debug("Refresh token validated");
        return refreshToken;
    }


    @Transactional
    public void revokeRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new UnauthorizedException("Invalid refresh token")
                        );
        refreshTokenRepository.delete(refreshToken);
        log.info("Refresh token revoked for user id={}", refreshToken.getUserId());
    }
}
