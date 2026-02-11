package tech.zeta.Digital_Fixed_Deposit_System.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebConfigTest {

    @Test
    void corsConfiguration_allowsLocalhost3000() {
        CorsConfigurationSource source = new WebConfig().corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/auth/login");
        request.addHeader("Origin", "http://localhost:3000");

        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertNotNull(configuration);
        assertTrue(configuration.getAllowedOrigins().contains("http://localhost:3000"));
        assertTrue(configuration.getAllowCredentials());
    }
}
