package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ITokenServiceTest {

    @Test
    void logger_isAvailable() {
        assertNotNull(ITokenService.logger);
    }
}
