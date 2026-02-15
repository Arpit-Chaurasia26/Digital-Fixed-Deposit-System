package tech.zeta.Digital_Fixed_Deposit_System.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
/*
Author : Priyanshu Mishra
*/

@Configuration
public class PasswordConfig {

    private static final Logger logger = LogManager.getLogger(PasswordConfig.class);
    private static final String ENCODER_PROPERTY = "app.password.encoder";

    // Password encoder bean used across the application for hashing and verifying passwords.
    @Bean
    public PasswordEncoder passwordEncoder() {
        String encoderType = System.getProperty(ENCODER_PROPERTY, "bcrypt");
        if ("noop".equalsIgnoreCase(encoderType)) {
            logger.info("PasswordEncoder bean created: noop");
            return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
        }

        logger.info("PasswordEncoder bean created: bcrypt");
        return new BCryptPasswordEncoder();
    }
}
