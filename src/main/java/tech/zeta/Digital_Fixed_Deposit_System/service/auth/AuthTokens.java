package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {
    private static final Logger logger = LogManager.getLogger(AuthTokens.class);

    public AuthTokens {
        logger.debug("AuthTokens created");
    }
}
