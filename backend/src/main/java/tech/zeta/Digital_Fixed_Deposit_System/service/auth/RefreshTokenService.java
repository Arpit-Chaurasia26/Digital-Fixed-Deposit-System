package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

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

/*
Author : Priyanshu Mishra
*/


@Service
public class RefreshTokenService implements IRefreshTokenService{

    private static final Logger logger = LogManager.getLogger(RefreshTokenService.class);
    private static final long REFRESH_TOKEN_DAYS = 7;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @Override
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
       logger.info("Refresh token created for user id={}", userId);
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
            logger.warn("Refresh token revoked");
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            logger.warn("Refresh token expired");
            throw new UnauthorizedException("Refresh token has expired");
        }

        logger.debug("Refresh token validated");
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
        logger.info("Refresh token revoked for user id={}", refreshToken.getUserId());
    }
}
