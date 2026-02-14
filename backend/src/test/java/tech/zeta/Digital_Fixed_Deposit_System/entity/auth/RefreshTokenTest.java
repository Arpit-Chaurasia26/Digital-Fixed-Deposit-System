package tech.zeta.Digital_Fixed_Deposit_System.entity.auth;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
Author : Priyanshu Mishra
*/


public class RefreshTokenTest {

    @Test
    void isExpired_returnsTrueWhenPast() {
        RefreshToken token = RefreshToken.builder()
                .userId(1L)
                .token("t")
                .expiresAt(Instant.now().minusSeconds(10))
                .createdAt(Instant.now())
                .revoked(false)
                .build();

        assertTrue(token.isExpired());
    }

    @Test
    void isExpired_returnsFalseWhenFuture() {
        RefreshToken token = RefreshToken.builder()
                .userId(1L)
                .token("t")
                .expiresAt(Instant.now().plusSeconds(10))
                .createdAt(Instant.now())
                .revoked(false)
                .build();

        assertFalse(token.isExpired());
    }
}
