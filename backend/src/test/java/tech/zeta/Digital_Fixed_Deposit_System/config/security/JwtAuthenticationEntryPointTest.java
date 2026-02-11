package tech.zeta.Digital_Fixed_Deposit_System.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtAuthenticationEntryPointTest {

    @Test
    void commence_writesUnauthorizedResponse() throws Exception {
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/secure");
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new AuthenticationException("auth") {});

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Unauthorized"));
    }
}
