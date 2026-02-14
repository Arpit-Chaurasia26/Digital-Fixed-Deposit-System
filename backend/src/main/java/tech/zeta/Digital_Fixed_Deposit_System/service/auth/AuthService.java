/*
Author : Priyanshu Mishra
*/

package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.LoginRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.RegisterRequest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.EmailOtp;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.RefreshToken;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;
import tech.zeta.Digital_Fixed_Deposit_System.exception.AccountLockedException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.EmailOtpRepository;
import tech.zeta.Digital_Fixed_Deposit_System.repository.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final IRefreshTokenService iRefreshTokenService;
    private final EmailOtpRepository emailOtpRepository;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService,
            IRefreshTokenService iRefreshTokenService, EmailOtpRepository emailOtpRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.iRefreshTokenService = iRefreshTokenService;
        this.emailOtpRepository = emailOtpRepository;
    }

    //  REGISTER

    @Transactional
    public AuthTokens register(RegisterRequest request) {

        logger.info("Register request received");

        boolean isVerified = emailOtpRepository
                .existsByEmailAndVerifiedTrue(request.getEmail());

        if (!isVerified) {
            throw new BusinessException("Email not verified");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Register blocked: email already registered");
            throw new BusinessException("Email already registered");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        );

        userRepository.save(user);

//        delete OTP records after success
        emailOtpRepository.deleteByEmail(request.getEmail());
        logger.info("User registered with id={}", user.getId());

        String accessToken =
                tokenService.generateAccessToken(
                        user.getId(),
                        user.getRole().name()
                );

        RefreshToken refreshToken =
                iRefreshTokenService.createRefreshToken(user.getId());

        logger.debug("Auth tokens generated for user id={}", user.getId());
        return new AuthTokens(accessToken, refreshToken.getToken());
    }

    // LOGIN

    @Transactional(noRollbackFor = { UnauthorizedException.class, AccountLockedException.class })
    public AuthTokens login(LoginRequest request) {

        logger.info("Login request received");

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid email or password")
                );

        // CHECK BLOCK
        if (user.getLoginBlockedUntil() != null &&
                user.getLoginBlockedUntil().isAfter(LocalDateTime.now())) {

            long remainingSeconds = Duration.between(
                    LocalDateTime.now(),
                    user.getLoginBlockedUntil()
            ).getSeconds();

            throw new AccountLockedException(
                    "Account temporarily locked",
                    remainingSeconds
            );
        }

        // WRONG PASSWORD
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= 3) {
                user.setLoginBlockedUntil(LocalDateTime.now().plusSeconds(30));
                user.setFailedLoginAttempts(0);
                userRepository.save(user);

                long remainingSeconds = 30;
                throw new AccountLockedException(
                        "Account temporarily locked",
                        remainingSeconds
                );
            }

            userRepository.save(user);
            throw new UnauthorizedException("Invalid email or password");
        }

        // SUCCESS — RESET STATE
        user.setFailedLoginAttempts(0);
        user.setLoginBlockedUntil(null);
        userRepository.save(user);

        String accessToken =
                tokenService.generateAccessToken(
                        user.getId(),
                        user.getRole().name()
                );

        RefreshToken refreshToken =
                iRefreshTokenService.createRefreshToken(user.getId());

        return new AuthTokens(accessToken, refreshToken.getToken());
    }

    //  REFRESH

    @Transactional
    public AuthTokens refresh(String refreshTokenValue) {
        logger.info("Refresh request received");

        RefreshToken refreshToken =
                iRefreshTokenService.validateRefreshToken(refreshTokenValue);

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() ->
                        new UnauthorizedException("User not found")
                );

        String accessToken = tokenService.generateAccessToken(
                user.getId(),
                user.getRole().name()
        );

        iRefreshTokenService.revokeRefreshToken(refreshTokenValue);
        refreshToken = iRefreshTokenService.createRefreshToken(user.getId());

        logger.info("Refresh token rotated for user id={}", user.getId());
        return new AuthTokens(accessToken, refreshToken.getToken());
    }

    // LOGOUT

    @Transactional
    public void logout(String refreshTokenValue) {
        logger.info("Logout request received");
        iRefreshTokenService.revokeRefreshToken(refreshTokenValue);
        logger.info("Logout completed");
    }

    //  PASSWORD RESET

    @Transactional(noRollbackFor = { BusinessException.class, AccountLockedException.class })
    public void resetPassword(String email, String otp, String newPassword) {
        logger.info("Password reset flow started for email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException("User not found")
                );

        // Check if blocked
        if (user.getLoginBlockedUntil() != null &&
                user.getLoginBlockedUntil().isAfter(LocalDateTime.now())) {

            long remainingSeconds = Duration.between(
                    LocalDateTime.now(),
                    user.getLoginBlockedUntil()
            ).getSeconds();

            throw new AccountLockedException(
                    "Password reset temporarily blocked",
                    remainingSeconds
            );
        }

        try {
            EmailOtp record = emailOtpRepository
                    .findByEmailAndOtpAndVerifiedFalse(email, otp)
                    .orElseThrow(() ->
                            new BusinessException("Invalid OTP")
                    );

            if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new BusinessException("OTP expired");
            }

            // Reset password
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setFailedPasswordResetAttempts(0);
            user.setLoginBlockedUntil(null);
            userRepository.save(user);

            record.setVerified(true);
            emailOtpRepository.save(record);

            logger.info("Password reset completed for user id={}", user.getId());
        } catch (BusinessException ex) {

            // FAILED ATTEMPT
            int attempts = user.getFailedPasswordResetAttempts() + 1;
            user.setFailedPasswordResetAttempts(attempts);

            if (attempts >= 3) {
                user.setLoginBlockedUntil(LocalDateTime.now().plusMinutes(5));
            }

            userRepository.save(user);
            logger.warn("Password reset failed for user id={}, attempts={}", user.getId(), attempts);
            throw ex;
        }
    }


}
