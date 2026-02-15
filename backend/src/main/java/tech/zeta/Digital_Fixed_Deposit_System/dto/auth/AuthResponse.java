package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * @author Priyanshu Mishra
 */

@Slf4j
public class AuthResponse {


    private final String accessToken;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
        log.debug("AuthResponse created");
    }

    public String getAccessToken() {
        return accessToken;
    }
}
