package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
Author : Priyanshu Mishra
*/


public class AuthTokensTest {

    @Test
    void createsRecordWithExpectedValues() {
        AuthTokens tokens = new AuthTokens("access", "refresh");

        assertEquals("access", tokens.accessToken());
        assertEquals("refresh", tokens.refreshToken());
    }
}
