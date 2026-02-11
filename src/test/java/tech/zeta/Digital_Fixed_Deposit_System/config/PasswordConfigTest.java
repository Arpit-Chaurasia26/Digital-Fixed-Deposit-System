package tech.zeta.Digital_Fixed_Deposit_System.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordConfigTest {

    @Test
    void passwordEncoder_encodesAndMatches() {
        PasswordEncoder encoder = new PasswordConfig().passwordEncoder();

        String hash = encoder.encode("secret");

        assertTrue(encoder.matches("secret", hash));
    }
}
