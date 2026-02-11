package tech.zeta.Digital_Fixed_Deposit_System.service.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.util.ReflectionTestUtils;
import tech.zeta.Digital_Fixed_Deposit_System.config.PasswordConfig;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.LoginRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.RegisterRequest;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Import({PasswordConfig.class, TokenService.class, RefreshTokenService.class, AuthService.class})
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void register_createsUserAndTokens() {
        RegisterRequest request = new RegisterRequest();
        ReflectionTestUtils.setField(request, "name", "Alice");
        ReflectionTestUtils.setField(request, "email", "alice@example.com");
        ReflectionTestUtils.setField(request, "password", "Password@123");

        AuthTokens tokens = authService.register(request);

        assertNotNull(tokens.accessToken());
        assertNotNull(tokens.refreshToken());
    }

    @Test
    void login_rejectsInvalidPassword() {
        RegisterRequest register = new RegisterRequest();
        ReflectionTestUtils.setField(register, "name", "Bob");
        ReflectionTestUtils.setField(register, "email", "bob@example.com");
        ReflectionTestUtils.setField(register, "password", "Password@123");
        authService.register(register);

        LoginRequest login = new LoginRequest();
        ReflectionTestUtils.setField(login, "email", "bob@example.com");
        ReflectionTestUtils.setField(login, "password", "Wrong@123");

        assertThrows(UnauthorizedException.class, () -> authService.login(login));
    }
}
