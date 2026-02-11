package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthResponse {

    private static final Logger logger = LogManager.getLogger(AuthResponse.class);

    private final String accessToken;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
        logger.debug("AuthResponse created");
    }

    public String getAccessToken() {
        return accessToken;
    }
}
