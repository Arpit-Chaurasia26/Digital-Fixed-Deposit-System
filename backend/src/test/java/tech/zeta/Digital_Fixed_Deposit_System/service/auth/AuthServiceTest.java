package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Priyanshu Mishra
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TokenService tokenService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private EmailOtpRepository emailOtpRepository;

    @InjectMocks
    private AuthService authService;

    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD = "P@ssw0rd";
    private static final Long USER_ID = 1L;

    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        refreshToken = RefreshToken.builder()
                .id(10L)
                .token("refresh-token")
                .userId(USER_ID)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();
    }

    @Nested
    @DisplayName("Register")
    class RegisterTests {

        @Test
        void register_rejectsUnverifiedEmail() {
            RegisterRequest request = new RegisterRequest();
            setField(request, "name", "User");
            setField(request, "email", EMAIL);
            setField(request, "password", PASSWORD);

            when(emailOtpRepository.existsByEmailAndVerifiedTrue(EMAIL)).thenReturn(false);

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Email not verified");
        }

        @Test
        void register_rejectsExistingEmail() {
            RegisterRequest request = new RegisterRequest();
            setField(request, "name", "User");
            setField(request, "email", EMAIL);
            setField(request, "password", PASSWORD);

            when(emailOtpRepository.existsByEmailAndVerifiedTrue(EMAIL)).thenReturn(true);
            when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("already registered");
        }

        @Test
        void register_successful() {
            RegisterRequest request = new RegisterRequest();
            setField(request, "name", "User");
            setField(request, "email", EMAIL);
            setField(request, "password", PASSWORD);

            when(emailOtpRepository.existsByEmailAndVerifiedTrue(EMAIL)).thenReturn(true);
            when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(passwordEncoder.encode(PASSWORD)).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                setField(u, "id", USER_ID);
                return u;
            });
            when(tokenService.generateAccessToken(USER_ID, Role.USER.name())).thenReturn("access");
            when(refreshTokenService.createRefreshToken(USER_ID)).thenReturn(refreshToken);

            AuthTokens tokens = authService.register(request);

            assertThat(tokens.accessToken()).isEqualTo("access");
            assertThat(tokens.refreshToken()).isEqualTo("refresh-token");
            verify(emailOtpRepository).deleteByEmail(EMAIL);
        }

        @Test
        void register_nullRequest_throwsNullPointer() {
            assertThatThrownBy(() -> authService.register(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Login")
    class LoginTests {
        @Test
        void login_rejectsMissingUser() {
            LoginRequest request = new LoginRequest();
            setField(request, "email", EMAIL);
            setField(request, "password", PASSWORD);

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(UnauthorizedException.class);
        }

        @Test
        void login_rejectsWrongPassword() {
            LoginRequest request = new LoginRequest();
            setField(request, "email", EMAIL);
            setField(request, "password", PASSWORD);

            User user = new User("User", EMAIL, "encoded", Role.USER);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, "encoded")).thenReturn(false);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(UnauthorizedException.class);
        }

        @Test
        void login_nullRequest_throwsNullPointer() {
            assertThatThrownBy(() -> authService.login(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void login_whenBlocked_throwsAccountLocked() {
            LoginRequest request = new LoginRequest();
            setField(request, "email", EMAIL);
            setField(request, "password", PASSWORD);

            User user = new User("User", EMAIL, "encoded", Role.USER);
            user.setLoginBlockedUntil(java.time.LocalDateTime.now().plusSeconds(60));
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(AccountLockedException.class)
                    .hasMessageContaining("Account temporarily locked");
        }

        @Test
        void login_wrongPassword_incrementsAttempts() {
            LoginRequest request = new LoginRequest();
            setField(request, "email", EMAIL);
            setField(request, "password", PASSWORD);

            User user = new User("User", EMAIL, "encoded", Role.USER);
            user.setFailedLoginAttempts(1);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, "encoded")).thenReturn(false);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(UnauthorizedException.class);

            assertThat(user.getFailedLoginAttempts()).isEqualTo(2);
            verify(userRepository).save(user);
        }

        @Test
        void login_wrongPassword_locksOnThirdAttempt() {
            LoginRequest request = new LoginRequest();
            setField(request, "email", EMAIL);
            setField(request, "password", PASSWORD);

            User user = new User("User", EMAIL, "encoded", Role.USER);
            user.setFailedLoginAttempts(2);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, "encoded")).thenReturn(false);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(AccountLockedException.class);

            assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
            assertThat(user.getLoginBlockedUntil()).isNotNull();
            verify(userRepository).save(user);
        }

        @Test
        void login_success_resetsAttempts() {
            LoginRequest request = new LoginRequest();
            setField(request, "email", EMAIL);
            setField(request, "password", PASSWORD);

            User user = new User("User", EMAIL, "encoded", Role.USER);
            user.setFailedLoginAttempts(2);
            user.setLoginBlockedUntil(java.time.LocalDateTime.now().minusSeconds(10));
            setField(user, "id", USER_ID);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, "encoded")).thenReturn(true);
            when(tokenService.generateAccessToken(USER_ID, Role.USER.name())).thenReturn("access");
            when(refreshTokenService.createRefreshToken(USER_ID)).thenReturn(refreshToken);

            AuthTokens tokens = authService.login(request);

            assertThat(tokens.accessToken()).isEqualTo("access");
            assertThat(user.getFailedLoginAttempts()).isZero();
            assertThat(user.getLoginBlockedUntil()).isNull();
            verify(userRepository).save(user);
        }
    }

    @Test
    void logout_revokesRefreshToken() {
        doNothing().when(refreshTokenService).revokeRefreshToken("refresh-token");
        authService.logout("refresh-token");
        verify(refreshTokenService).revokeRefreshToken("refresh-token");
    }

    @Test
    void refresh_success_returnsAccessToken() {
        RefreshToken token = RefreshToken.builder()
                .id(1L)
                .userId(USER_ID)
                .token("refresh")
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .revoked(false)
                .build();
        User user = new User("User", EMAIL, "hash", Role.USER);
        setField(user, "id", USER_ID);

        when(refreshTokenService.validateRefreshToken("refresh")).thenReturn(token);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(tokenService.generateAccessToken(USER_ID, Role.USER.name())).thenReturn("access");
        when(refreshTokenService.createRefreshToken(USER_ID)).thenReturn(refreshToken);

        AuthTokens tokens = authService.refresh("refresh");

        assertThat(tokens.accessToken()).isEqualTo("access");
        assertThat(tokens.refreshToken()).isEqualTo("refresh-token");
        verify(refreshTokenService).revokeRefreshToken("refresh");
    }

    @Test
    void refresh_rejectsMissingUser() {
        RefreshToken token = RefreshToken.builder()
                .id(1L)
                .userId(999L)
                .token("refresh")
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .revoked(false)
                .build();
        when(refreshTokenService.validateRefreshToken("refresh")).thenReturn(token);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("refresh"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("User not found");
    }

    @Nested
    @DisplayName("Reset Password")
    class ResetPasswordTests {

        @Test
        void resetPassword_userNotFound() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.resetPassword(EMAIL, "123456", "New@123"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("User not found");
        }

        @Test
        void resetPassword_blocked_throwsAccountLocked() {
            User user = new User("User", EMAIL, "hash", Role.USER);
            user.setLoginBlockedUntil(java.time.LocalDateTime.now().plusSeconds(60));
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> authService.resetPassword(EMAIL, "123456", "New@123"))
                    .isInstanceOf(AccountLockedException.class)
                    .hasMessageContaining("Password reset temporarily blocked");
        }

        @Test
        void resetPassword_invalidOtp_incrementsAttempts() {
            User user = new User("User", EMAIL, "hash", Role.USER);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(emailOtpRepository.findByEmailAndOtpAndVerifiedFalse(EMAIL, "bad"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.resetPassword(EMAIL, "bad", "New@123"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Invalid OTP");

            assertThat(user.getFailedPasswordResetAttempts()).isEqualTo(1);
            verify(userRepository).save(user);
        }

        @Test
        void resetPassword_expiredOtp_incrementsAttempts() {
            User user = new User("User", EMAIL, "hash", Role.USER);
            EmailOtp otp = EmailOtp.builder()
                    .email(EMAIL)
                    .otp("123456")
                    .verified(false)
                    .expiresAt(java.time.LocalDateTime.now().minusMinutes(1))
                    .build();

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(emailOtpRepository.findByEmailAndOtpAndVerifiedFalse(EMAIL, "123456"))
                    .thenReturn(Optional.of(otp));

            assertThatThrownBy(() -> authService.resetPassword(EMAIL, "123456", "New@123"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("OTP expired");

            assertThat(user.getFailedPasswordResetAttempts()).isEqualTo(1);
            verify(userRepository).save(user);
        }

        @Test
        void resetPassword_locksAfterThreeFailures() {
            User user = new User("User", EMAIL, "hash", Role.USER);
            user.setFailedPasswordResetAttempts(2);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(emailOtpRepository.findByEmailAndOtpAndVerifiedFalse(EMAIL, "bad"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.resetPassword(EMAIL, "bad", "New@123"))
                    .isInstanceOf(BusinessException.class);

            assertThat(user.getFailedPasswordResetAttempts()).isEqualTo(3);
            assertThat(user.getLoginBlockedUntil()).isNotNull();
            verify(userRepository).save(user);
        }

        @Test
        void resetPassword_success_resetsState() {
            User user = new User("User", EMAIL, "hash", Role.USER);
            EmailOtp otp = EmailOtp.builder()
                    .email(EMAIL)
                    .otp("123456")
                    .verified(false)
                    .expiresAt(java.time.LocalDateTime.now().plusMinutes(5))
                    .build();

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(emailOtpRepository.findByEmailAndOtpAndVerifiedFalse(EMAIL, "123456"))
                    .thenReturn(Optional.of(otp));
            when(passwordEncoder.encode("New@123")).thenReturn("encoded");

            authService.resetPassword(EMAIL, "123456", "New@123");

            assertThat(user.getPassword()).isEqualTo("encoded");
            assertThat(user.getFailedPasswordResetAttempts()).isZero();
            assertThat(user.getLoginBlockedUntil()).isNull();
            assertThat(otp.isVerified()).isTrue();
            verify(userRepository).save(user);
            verify(emailOtpRepository).save(otp);
        }
    }

    private void setField(Object target, String field, Object value) {
        try {
            var f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            // no-op for test setup
        }
    }
}
