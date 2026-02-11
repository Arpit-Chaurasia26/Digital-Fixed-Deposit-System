package tech.zeta.Digital_Fixed_Deposit_System.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.LoginRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.RegisterRequest;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.AuthService;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.AuthTokens;
import tech.zeta.Digital_Fixed_Deposit_System.util.CookieUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    public AuthController(AuthService authService, CookieUtil cookieUtil) {
        this.authService = authService;
        this.cookieUtil = cookieUtil;
    }

    //  REGISTER

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        logger.info("Register endpoint called");
        AuthTokens tokens = authService.register(request);

        cookieUtil.setAccessToken(response, tokens.accessToken());
        cookieUtil.setRefreshToken(response, tokens.refreshToken());

        logger.info("Register endpoint completed");
        return ResponseEntity.ok().build();
    }

    // LOGIN

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        logger.info("Login endpoint called");
        AuthTokens tokens = authService.login(request);

        cookieUtil.setAccessToken(response, tokens.accessToken());
        cookieUtil.setRefreshToken(response, tokens.refreshToken());

        logger.info("Login endpoint completed");
        return ResponseEntity.ok().build();
    }

    // REFRESH

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        logger.info("Refresh endpoint called");
        String refreshToken = cookieUtil.extractRefreshToken(request);

        if (refreshToken == null) {
            cookieUtil.clearAuthCookies(response);
            logger.warn("Refresh token missing");
            throw new UnauthorizedException("Refresh token missing");
        }

        try {
            AuthTokens tokens = authService.refresh(refreshToken);
            cookieUtil.setAccessToken(response, tokens.accessToken());
            cookieUtil.setRefreshToken(response, tokens.refreshToken());

            logger.info("Refresh endpoint completed");
            return ResponseEntity.ok().build();
        } catch (UnauthorizedException ex) {
            cookieUtil.clearAuthCookies(response);
            logger.warn("Refresh token invalid");
            throw ex;
        }
    }

    // LOGOUT

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        logger.info("Logout endpoint called");
        String refreshToken = cookieUtil.extractRefreshToken(request);

        if (refreshToken != null) {
            try {
                authService.logout(refreshToken);
            } catch (UnauthorizedException ex) {
                // Idempotent logout: ignore invalid/expired token
                logger.debug("Logout ignored: token invalid or expired");
            }
        }

        cookieUtil.clearAuthCookies(response);
        logger.info("Logout endpoint completed");
        return ResponseEntity.noContent().build();
    }
}
