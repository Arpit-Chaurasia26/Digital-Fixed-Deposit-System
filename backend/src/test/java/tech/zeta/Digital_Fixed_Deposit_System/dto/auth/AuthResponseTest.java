package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Priyanshu Mishra
 */

public class AuthResponseTest {

    @Test
    void returnsAccessToken() {
        AuthResponse response = new AuthResponse("token");

        assertEquals("token", response.getAccessToken());
    }
}
