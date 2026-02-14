package tech.zeta.Digital_Fixed_Deposit_System.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
Author : Priyanshu Mishra
*/


public class WebConfigTest {

    private static final String ORIGINS_PROPERTY = "app.cors.allowed-origins";

    @Test
    void corsConfiguration_allowsLocalhost3000() {
        System.clearProperty(ORIGINS_PROPERTY);
        CorsConfigurationSource source = new WebConfig().corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/auth/login");
        request.addHeader("Origin", "http://localhost:3000");

        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertNotNull(configuration);
        assertTrue(configuration.getAllowedOrigins().contains("http://localhost:3000"));
        assertTrue(configuration.getAllowCredentials());
    }

    @Test
    void corsConfiguration_usesCustomOrigins() {
        System.setProperty(ORIGINS_PROPERTY, "https://example.com, https://app.example.com");
        try {
            CorsConfigurationSource source = new WebConfig().corsConfigurationSource();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/auth/login");
            request.addHeader("Origin", "https://example.com");

            CorsConfiguration configuration = source.getCorsConfiguration(request);

            assertNotNull(configuration);
            assertTrue(configuration.getAllowedOrigins().contains("https://example.com"));
            assertTrue(configuration.getAllowedOrigins().contains("https://app.example.com"));
        } finally {
            System.clearProperty(ORIGINS_PROPERTY);
        }
    }
}
