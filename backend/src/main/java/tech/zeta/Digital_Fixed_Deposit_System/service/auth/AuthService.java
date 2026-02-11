package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.LoginRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.RegisterRequest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.RefreshToken;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final IRefreshTokenService iRefreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService,
            IRefreshTokenService iRefreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.iRefreshTokenService = iRefreshTokenService;
    }

    //  REGISTER

    @Transactional
    public AuthTokens register(RegisterRequest request) {

        logger.info("Register request received");

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

    @Transactional
    public AuthTokens login(LoginRequest request) {

        logger.info("Login request received");

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid email or password")
                );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login failed: invalid credentials");
            throw new UnauthorizedException("Invalid email or password");
        }

        String accessToken =
                tokenService.generateAccessToken(
                        user.getId(),
                        user.getRole().name()
                );

        RefreshToken refreshToken =
                iRefreshTokenService.createRefreshToken(user.getId());

        logger.info("Login succeeded for user id={}", user.getId());
        return new AuthTokens(accessToken, refreshToken.getToken());
    }

    //  REFRESH

    @Transactional
    public AuthTokens refresh(String refreshTokenValue) {

        logger.info("Refresh token request received");

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

        RefreshToken newRefreshToken =
                iRefreshTokenService.createRefreshToken(user.getId());

        iRefreshTokenService.revokeRefreshToken(refreshTokenValue);

        logger.info("Refresh token rotated for user id={}", user.getId());
        return new AuthTokens(accessToken, newRefreshToken.getToken());
    }

    // ================= LOGOUT =================

    @Transactional
    public void logout(String refreshTokenValue) {
        logger.info("Logout request received");
        iRefreshTokenService.revokeRefreshToken(refreshTokenValue);
        logger.info("Logout completed");
    }
}
