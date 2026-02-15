package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Priyanshu Mishra
 */


@Slf4j
public record AuthTokens(
        String accessToken,
        String refreshToken
) {

    public AuthTokens {
        log.debug("AuthTokens created");
    }
}
