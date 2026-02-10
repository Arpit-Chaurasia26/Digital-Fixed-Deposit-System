package tech.zeta.Digital_Fixed_Deposit_System.service.auth;


public record AuthTokens(
        String accessToken,
        String refreshToken
) {}
