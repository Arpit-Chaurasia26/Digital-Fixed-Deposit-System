package tech.zeta.Digital_Fixed_Deposit_System.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.RefreshToken;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;
import tech.zeta.Digital_Fixed_Deposit_System.repository.UserRepository;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.RefreshTokenService;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.TokenService;
import tech.zeta.Digital_Fixed_Deposit_System.util.CookieUtil;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Priyanshu Mishra
 */

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("OAuthSuccessHandler Tests")
class OAuthSuccessHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private OAuthSuccessHandler oAuthSuccessHandler;

    private User existingUser;
    private RefreshToken mockRefreshToken;

    @BeforeEach
    void setUp() {
        existingUser = new User("Test User", "test@example.com", "OAUTH_USER", Role.USER);
        ReflectionTestUtils.setField(existingUser, "id", 1L);
        existingUser.setAuthProvider("GOOGLE");
        existingUser.setProviderId("test@example.com");
        existingUser.setEmailVerified(true);

        mockRefreshToken = RefreshToken.builder()
                .id(1L)
                .userId(1L)
                .token("mock-refresh-token")
                .expiresAt(Instant.now().plusSeconds(604800))
                .revoked(false)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should create new user when user does not exist")
    void onAuthenticationSuccess_newUser_createsUserAndRedirects() throws IOException {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "newuser@example.com");
        attributes.put("name", "New User");

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("newuser@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("New User");
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedUser, "id", 2L);
            return savedUser;
        });

        when(tokenService.generateAccessToken(2L, "USER")).thenReturn("mock-access-token");
        when(refreshTokenService.createRefreshToken(2L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        User capturedUser = userCaptor.getValue();
        assertEquals("New User", capturedUser.getName());
        assertEquals("newuser@example.com", capturedUser.getEmail());
        assertEquals("OAUTH_USER", capturedUser.getPassword());
        assertEquals(Role.USER, capturedUser.getRole());
        assertEquals("GOOGLE", capturedUser.getAuthProvider());
        assertEquals("newuser@example.com", capturedUser.getProviderId());
        assertTrue(capturedUser.isEmailVerified());

        verify(userRepository).save(any(User.class));
        verify(cookieUtil).setAccessToken(response, "mock-access-token");
        verify(cookieUtil).setRefreshToken(response, "mock-refresh-token");
    }

    @Test
    @DisplayName("Should use existing user when user already exists")
    void onAuthenticationSuccess_existingUser_usesExistingUserAndRedirects() throws IOException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("test@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("Test User");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        when(tokenService.generateAccessToken(1L, "USER")).thenReturn("mock-access-token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository).findByEmail("test@example.com");
        verify(cookieUtil).setAccessToken(response, "mock-access-token");
        verify(cookieUtil).setRefreshToken(response, "mock-refresh-token");
    }

    @Test
    @DisplayName("Should handle OAuth user with admin role")
    void onAuthenticationSuccess_adminUser_generatesAdminToken() throws IOException {
        // Arrange
        User adminUser = new User("Admin User", "admin@example.com", "OAUTH_USER", Role.ADMIN);
        ReflectionTestUtils.setField(adminUser, "id", 3L);
        adminUser.setAuthProvider("GOOGLE");
        adminUser.setProviderId("admin@example.com");
        adminUser.setEmailVerified(true);

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("admin@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("Admin User");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        when(tokenService.generateAccessToken(3L, "ADMIN")).thenReturn("mock-admin-access-token");
        when(refreshTokenService.createRefreshToken(3L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(tokenService).generateAccessToken(3L, "ADMIN");
        verify(cookieUtil).setAccessToken(response, "mock-admin-access-token");
        verify(cookieUtil).setRefreshToken(response, "mock-refresh-token");
    }

    @Test
    @DisplayName("Should handle null email attribute gracefully")
    void onAuthenticationSuccess_nullEmail_handlesGracefully() throws IOException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn(null);
        when(oAuth2User.getAttribute("name")).thenReturn("User Without Email");
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedUser, "id", 4L);
            return savedUser;
        });

        when(tokenService.generateAccessToken(4L, "USER")).thenReturn("mock-access-token");
        when(refreshTokenService.createRefreshToken(4L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        User capturedUser = userCaptor.getValue();
        assertNull(capturedUser.getEmail());
        assertEquals("User Without Email", capturedUser.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle null name attribute gracefully")
    void onAuthenticationSuccess_nullName_handlesGracefully() throws IOException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("noname@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn(null);
        when(userRepository.findByEmail("noname@example.com")).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedUser, "id", 5L);
            return savedUser;
        });

        when(tokenService.generateAccessToken(5L, "USER")).thenReturn("mock-access-token");
        when(refreshTokenService.createRefreshToken(5L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        User capturedUser = userCaptor.getValue();
        assertNull(capturedUser.getName());
        assertEquals("noname@example.com", capturedUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should set cookies with correct tokens")
    void onAuthenticationSuccess_setCookies_callsCookieUtil() throws IOException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("test@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("Test User");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        when(tokenService.generateAccessToken(1L, "USER")).thenReturn("access-token-123");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(cookieUtil).setAccessToken(response, "access-token-123");
        verify(cookieUtil).setRefreshToken(response, "mock-refresh-token");
    }

    @Test
    @DisplayName("Should redirect to correct frontend URL")
    void onAuthenticationSuccess_redirect_correctURL() throws IOException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("test@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("Test User");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        when(tokenService.generateAccessToken(1L, "USER")).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert - verify cookies were set, redirect is internal
        verify(cookieUtil).setAccessToken(response, "access-token");
        verify(cookieUtil).setRefreshToken(response, "mock-refresh-token");
    }

    @Test
    @DisplayName("Should create user with correct OAuth defaults")
    void onAuthenticationSuccess_newUser_setsOAuthDefaults() throws IOException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("oauth@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("OAuth User");
        when(userRepository.findByEmail("oauth@example.com")).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedUser, "id", 6L);
            return savedUser;
        });

        when(tokenService.generateAccessToken(6L, "USER")).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(6L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        User capturedUser = userCaptor.getValue();
        assertEquals("GOOGLE", capturedUser.getAuthProvider());
        assertEquals("oauth@example.com", capturedUser.getProviderId());
        assertEquals("OAUTH_USER", capturedUser.getPassword());
        assertTrue(capturedUser.isEmailVerified());
        assertEquals(Role.USER, capturedUser.getRole());
    }


    @Test
    @DisplayName("Should handle user with all fields populated")
    void onAuthenticationSuccess_fullUserProfile_handlesCorrectly() throws IOException {
        // Arrange
        User fullUser = new User("Full User", "full@example.com", "OAUTH_USER", Role.USER);
        ReflectionTestUtils.setField(fullUser, "id", 7L);
        fullUser.setAuthProvider("GOOGLE");
        fullUser.setProviderId("full@example.com");
        fullUser.setEmailVerified(true);
        fullUser.setFailedLoginAttempts(0);
        fullUser.setFailedPasswordResetAttempts(0);

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("full@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("Full User");
        when(userRepository.findByEmail("full@example.com")).thenReturn(Optional.of(fullUser));

        when(tokenService.generateAccessToken(7L, "USER")).thenReturn("full-access-token");
        when(refreshTokenService.createRefreshToken(7L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(cookieUtil).setAccessToken(response, "full-access-token");
        verify(cookieUtil).setRefreshToken(response, "mock-refresh-token");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle different OAuth providers")
    void onAuthenticationSuccess_differentProvider_handlesCorrectly() throws IOException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("github@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("GitHub User");
        when(userRepository.findByEmail("github@example.com")).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedUser, "id", 8L);
            return savedUser;
        });

        when(tokenService.generateAccessToken(8L, "USER")).thenReturn("github-token");
        when(refreshTokenService.createRefreshToken(8L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        User capturedUser = userCaptor.getValue();
        assertEquals("GOOGLE", capturedUser.getAuthProvider()); // Handler sets GOOGLE
        assertEquals("github@example.com", capturedUser.getProviderId());
        assertTrue(capturedUser.isEmailVerified());
    }

    @Test
    @DisplayName("Should verify token generation with correct parameters")
    void onAuthenticationSuccess_tokenGeneration_usesCorrectParams() throws IOException {
        // Arrange
        User userWithId = new User("User", "user@example.com", "OAUTH_USER", Role.USER);
        ReflectionTestUtils.setField(userWithId, "id", 123L);

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("user@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("User");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userWithId));

        when(tokenService.generateAccessToken(123L, "USER")).thenReturn("specific-token");
        when(refreshTokenService.createRefreshToken(123L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(tokenService).generateAccessToken(123L, "USER");
        verify(refreshTokenService).createRefreshToken(123L);
    }

    @Test
    @DisplayName("Should handle empty email string")
    void onAuthenticationSuccess_emptyEmail_handlesCorrectly() throws IOException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("");
        when(oAuth2User.getAttribute("name")).thenReturn("Empty Email User");
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedUser, "id", 9L);
            return savedUser;
        });

        when(tokenService.generateAccessToken(9L, "USER")).thenReturn("token");
        when(refreshTokenService.createRefreshToken(9L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        User capturedUser = userCaptor.getValue();
        assertEquals("", capturedUser.getEmail());
        assertEquals("", capturedUser.getProviderId());
    }

    @Test
    @DisplayName("Should handle empty name string")
    void onAuthenticationSuccess_emptyName_handlesCorrectly() throws IOException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("empty-name@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("");
        when(userRepository.findByEmail("empty-name@example.com")).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedUser, "id", 10L);
            return savedUser;
        });

        when(tokenService.generateAccessToken(10L, "USER")).thenReturn("token");
        when(refreshTokenService.createRefreshToken(10L)).thenReturn(mockRefreshToken);

        // Act
        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        User capturedUser = userCaptor.getValue();
        assertEquals("", capturedUser.getName());
        assertEquals("empty-name@example.com", capturedUser.getEmail());
    }
}

