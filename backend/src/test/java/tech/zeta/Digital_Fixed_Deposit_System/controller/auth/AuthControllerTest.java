package tech.zeta.Digital_Fixed_Deposit_System.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.LoginRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.RegisterRequest;
import tech.zeta.Digital_Fixed_Deposit_System.exception.ResourceNotFoundException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.AuthService;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.AuthTokens;
import tech.zeta.Digital_Fixed_Deposit_System.service.email.EmailOtpService;
import tech.zeta.Digital_Fixed_Deposit_System.service.user.UserService;
import tech.zeta.Digital_Fixed_Deposit_System.util.CookieUtil;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


/**
 * @author Priyanshu Mishra
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Mock private AuthService authService;
    @Mock private CookieUtil cookieUtil;
    @Mock private EmailOtpService emailOtpService;
    @Mock private UserService userService;;

    @InjectMocks
    private AuthController controller;

    @Test
    void register_setsCookies() {
        RegisterRequest request = new RegisterRequest();
        setField(request, "name", "User");
        setField(request, "email", "u@example.com");
        setField(request, "password", "Password@123");

        when(authService.register(any())).thenReturn(new AuthTokens("access", "refresh"));

        HttpServletResponse response = mock(HttpServletResponse.class);
        controller.register(request, response);

        verify(cookieUtil).setAccessToken(eq(response), anyString());
        verify(cookieUtil).setRefreshToken(eq(response), anyString());
    }

    @Test
    void login_setsCookies() {
        LoginRequest request = new LoginRequest();
        setField(request, "email", "u@example.com");
        setField(request, "password", "Password@123");

        when(authService.login(any())).thenReturn(new AuthTokens("access", "refresh"));

        HttpServletResponse response = mock(HttpServletResponse.class);
        controller.login(request, response);

        verify(cookieUtil).setAccessToken(eq(response), anyString());
        verify(cookieUtil).setRefreshToken(eq(response), anyString());
    }

    @Test
    void refresh_missingCookie_throwsUnauthorized() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(cookieUtil.extractRefreshToken(request)).thenReturn(null);

        assertThatThrownBy(() -> controller.refresh(request, response))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Refresh token missing");

        verify(cookieUtil).clearAuthCookies(response);
    }

    @Test
    void logout_clearsCookies() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(cookieUtil.extractRefreshToken(request)).thenReturn("refresh");
        doNothing().when(authService).logout("refresh");

        controller.logout(request, response);

        verify(authService).logout("refresh");
        verify(cookieUtil).clearAuthCookies(response);
    }

    @Test
    void sendOtp_delegates() {
        controller.sendOtp("u@example.com");
        verify(emailOtpService).sendOtp("u@example.com");
    }

    @Test
    void verifyOtp_delegates() {
        controller.verifyOtp("u@example.com", "123456");
        verify(emailOtpService).verifyOtp("u@example.com", "123456");
    }

    @Test
    void sendPasswordResetOtp_requiresExistingEmail() {
        when(userService.checkByEmail("missing@example.com")).thenReturn(false);

        assertThatThrownBy(() -> controller.sendPasswordResetOtp("missing@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void resetPassword_delegates() {
        controller.resetPassword("u@example.com", "123456", "New@123");
        verify(authService).resetPassword("u@example.com", "123456", "New@123");
    }

    @Test
    void refresh_success_setsCookies() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(cookieUtil.extractRefreshToken(request)).thenReturn("refresh");
        when(authService.refresh("refresh")).thenReturn(new AuthTokens("access", "refresh"));

        controller.refresh(request, response);

        verify(cookieUtil).setAccessToken(eq(response), anyString());
        verify(cookieUtil).setRefreshToken(eq(response), anyString());
    }

    @Test
    void refresh_invalidToken_clearsCookiesAndRethrows() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(cookieUtil.extractRefreshToken(request)).thenReturn("refresh");
        when(authService.refresh("refresh")).thenThrow(new UnauthorizedException("invalid"));

        assertThatThrownBy(() -> controller.refresh(request, response))
                .isInstanceOf(UnauthorizedException.class);

        verify(cookieUtil).clearAuthCookies(response);
    }

    @Test
    void logout_ignoresInvalidToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(cookieUtil.extractRefreshToken(request)).thenReturn("refresh");
        doThrow(new UnauthorizedException("invalid")).when(authService).logout("refresh");

        controller.logout(request, response);

        verify(cookieUtil).clearAuthCookies(response);
    }

    @Test
    void sendPasswordResetOtp_succeedsWhenEmailExists() {
        when(userService.checkByEmail("u@example.com")).thenReturn(true);

        controller.sendPasswordResetOtp("u@example.com");

        verify(emailOtpService).sendOtp("u@example.com");
    }

    @Test
    void sendOtp_existingEmail_throws() {
        when(userService.checkByEmail("u@example.com")).thenReturn(true);

        assertThatThrownBy(() -> controller.sendOtp("u@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(emailOtpService, never()).sendOtp(anyString());
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
