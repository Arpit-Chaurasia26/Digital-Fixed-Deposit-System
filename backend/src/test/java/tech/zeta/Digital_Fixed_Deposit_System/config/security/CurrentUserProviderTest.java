package tech.zeta.Digital_Fixed_Deposit_System.config.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Priyanshu Mishra
 */

public class CurrentUserProviderTest {

    private final CurrentUserProvider provider = new CurrentUserProvider();

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_returnsPrincipal() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(10L, null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals(10L, provider.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_throwsWhenMissingAuthentication() {
        SecurityContextHolder.clearContext();

        assertThrows(UnauthorizedException.class, provider::getCurrentUserId);
    }
}
