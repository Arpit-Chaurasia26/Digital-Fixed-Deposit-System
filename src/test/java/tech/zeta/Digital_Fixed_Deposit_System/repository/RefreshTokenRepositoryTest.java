package tech.zeta.Digital_Fixed_Deposit_System.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.RefreshToken;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void findByToken_returnsToken() {
        RefreshToken token = RefreshToken.builder()
                .userId(1L)
                .token("token-1")
                .expiresAt(Instant.now().plusSeconds(60))
                .createdAt(Instant.now())
                .revoked(false)
                .build();
        refreshTokenRepository.save(token);

        assertTrue(refreshTokenRepository.findByToken("token-1").isPresent());
    }

    @Test
    void deleteByUserId_removesTokens() {
        RefreshToken token = RefreshToken.builder()
                .userId(2L)
                .token("token-2")
                .expiresAt(Instant.now().plusSeconds(60))
                .createdAt(Instant.now())
                .revoked(false)
                .build();
        refreshTokenRepository.save(token);

        refreshTokenRepository.deleteByUserId(2L);

        assertTrue(refreshTokenRepository.findByToken("token-2").isEmpty());
    }
}
