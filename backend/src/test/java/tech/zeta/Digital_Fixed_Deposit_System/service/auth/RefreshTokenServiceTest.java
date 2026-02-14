package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.RefreshToken;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.RefreshTokenRepository;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/*
Author : Priyanshu Mishra
*/


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RefreshTokenServiceTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void createRefreshToken_persistsToken() {
        RefreshToken token = refreshTokenService.createRefreshToken(5L);

        assertNotNull(token.getId());
        assertEquals(5L, token.getUserId());
        assertTrue(refreshTokenRepository.findByToken(token.getToken()).isPresent());
    }

    @Test
    void validateRefreshToken_rejectsExpiredToken() {
        RefreshToken expired = RefreshToken.builder()
                .userId(7L)
                .token("expired")
                .expiresAt(Instant.now().minusSeconds(5))
                .createdAt(Instant.now())
                .revoked(false)
                .build();
        refreshTokenRepository.save(expired);

        assertThrows(UnauthorizedException.class, () -> refreshTokenService.validateRefreshToken("expired"));
    }

    @Test
    void revokeRefreshToken_deletesToken() {
        RefreshToken token = refreshTokenService.createRefreshToken(9L);

        refreshTokenService.revokeRefreshToken(token.getToken());

        assertTrue(refreshTokenRepository.findByToken(token.getToken()).isEmpty());
    }
}
