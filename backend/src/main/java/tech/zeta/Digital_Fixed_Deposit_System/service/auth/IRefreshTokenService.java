package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.RefreshToken;

/*
Author : Priyanshu Mishra
*/


public interface IRefreshTokenService {

    Logger logger = LogManager.getLogger(IRefreshTokenService.class);

    //Creates and persists a refresh token for a user.

    RefreshToken createRefreshToken(Long userId);

    // Validates refresh token (exists, not revoked, not expired). Returns the RefreshToken entity if valid.
    @Transactional()
    RefreshToken validateRefreshToken(String token);

    // Revokes a refresh token (used during logout).

    void revokeRefreshToken(String token);
}
