package tech.zeta.Digital_Fixed_Deposit_System.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    private static final Logger logger = LogManager.getLogger(PasswordConfig.class);

    // Password encoder bean used across the application for hashing and verifying passwords.
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("PasswordEncoder bean created");
        return new BCryptPasswordEncoder();
    }
}
