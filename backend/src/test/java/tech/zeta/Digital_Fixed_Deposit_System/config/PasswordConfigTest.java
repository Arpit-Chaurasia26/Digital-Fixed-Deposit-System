package tech.zeta.Digital_Fixed_Deposit_System.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

/*
Author : Priyanshu Mishra
*/


public class PasswordConfigTest {

    private static final String ENCODER_PROPERTY = "app.password.encoder";

    @Test
    void passwordEncoder_encodesAndMatches() {
        System.clearProperty(ENCODER_PROPERTY);
        PasswordEncoder encoder = new PasswordConfig().passwordEncoder();

        String hash = encoder.encode("secret");

        assertTrue(encoder.matches("secret", hash));
    }

    @Test
    void passwordEncoder_noopUsesPlaintext() {
        System.setProperty(ENCODER_PROPERTY, "noop");
        try {
            PasswordEncoder encoder = new PasswordConfig().passwordEncoder();
            String hash = encoder.encode("secret");
            assertTrue(encoder.matches("secret", hash));
            assertTrue("secret".equals(hash));
        } finally {
            System.clearProperty(ENCODER_PROPERTY);
        }
    }
}
