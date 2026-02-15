package tech.zeta.Digital_Fixed_Deposit_System.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.TokenService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Priyanshu Mishra
 */

public class JwtAuthenticationFilterTest {

    private static final String SECRET = "VGhpcy1pcy1hLXRlc3Qtc2VjcmV0LWtleS1mb3Itand0";

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_allowsRequestWithoutToken() throws Exception {
        TokenService tokenService = new TokenService(SECRET);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        TestFilterChain chain = new TestFilterChain();

        filter.doFilter(request, response, chain);

        assertTrue(chain.wasCalled);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_setsAuthenticationForValidToken() throws Exception {
        TokenService tokenService = new TokenService(SECRET);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokenService.generateAccessToken(99L, "USER"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        TestFilterChain chain = new TestFilterChain();

        filter.doFilter(request, response, chain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(99L, authentication.getPrincipal());
        assertTrue(chain.wasCalled);
    }

    @Test
    void doFilter_clearsAuthenticationForInvalidToken() throws Exception {
        TokenService tokenService = new TokenService(SECRET);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid");
        MockHttpServletResponse response = new MockHttpServletResponse();

        TestFilterChain chain = new TestFilterChain();

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(chain.wasCalled);
    }

    private static final class TestFilterChain implements FilterChain {
        private boolean wasCalled;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) {
            this.wasCalled = true;
        }
    }
}
