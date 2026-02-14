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
import tech.zeta.Digital_Fixed_Deposit_System.exception.ResourceNotFoundException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.UserRepository;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.AuthService;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.AuthTokens;
import tech.zeta.Digital_Fixed_Deposit_System.service.email.EmailOtpService;
import tech.zeta.Digital_Fixed_Deposit_System.util.CookieUtil;

/*
Author : Priyanshu Mishra
*/


@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final EmailOtpService emailOtpService;
    private final UserRepository userRepository;


    public AuthController(AuthService authService, CookieUtil cookieUtil, EmailOtpService emailOtpService, UserRepository userRepository) {
        this.authService = authService;
        this.cookieUtil = cookieUtil;
        this.emailOtpService = emailOtpService;
        this.userRepository = userRepository;
    }

    // SEND OTP FOR EMAIL VERIFICATION

    @PostMapping("/email/send-otp")
    public ResponseEntity<Void> sendOtp(@RequestParam String email) {
        if (userRepository.existsByEmail(email)) {
            logger.warn("The email is already register with another account = {}", email);
            throw new ResourceNotFoundException("Account has been already registered with this email address");
        }
        logger.info("Send OTP requested for email={}", email);
        emailOtpService.sendOtp(email);
        logger.info("Send OTP completed for email={}", email);
        return ResponseEntity.ok().build();
    }


   // VERIFY OTP FOR EMAIL VERIFICATION

    @PostMapping("/email/verify-otp")
    public ResponseEntity<Void> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp
    ) {
        logger.info("Verify OTP requested for email={}", email);
        emailOtpService.verifyOtp(email, otp);
        return ResponseEntity.ok().build();
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

   // PASSWORD RESET - SEND OTP

    @PostMapping("/password/send-otp")
    public ResponseEntity<Void> sendPasswordResetOtp(
            @RequestParam String email
    ) {
        logger.info("Password reset OTP requested for email={}", email);
        if (!userRepository.existsByEmail(email)) {
            logger.warn("Password reset requested for unknown email={}", email);
            throw new ResourceNotFoundException("No account found with this email address");
        }
        emailOtpService.sendOtp(email);
        logger.info("Password reset OTP sent for email={}", email);
        return ResponseEntity.ok().build();
    }

    // PASSWORD RESET - VERIFY OTP

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword
    ) {
        logger.info("Password reset requested for email={}", email);
        authService.resetPassword(email, otp, newPassword);
        logger.info("Password reset completed for email={}", email);
        return ResponseEntity.ok().build();
    }

}
