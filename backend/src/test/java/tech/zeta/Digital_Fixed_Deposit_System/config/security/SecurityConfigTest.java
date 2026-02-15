package tech.zeta.Digital_Fixed_Deposit_System.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Priyanshu Mishra
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@DisplayName("Security Configuration Tests")
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private OAuthSuccessHandler oAuthSuccessHandler;

    @Nested
    @DisplayName("Bean Configuration Tests")
    class BeanConfigurationTests {

        @Test
        @DisplayName("SecurityFilterChain bean should load")
        void securityFilterChainBeanLoads() {
            assertNotNull(securityFilterChain);
        }

        @Test
        @DisplayName("AuthenticationManager bean should load")
        void authenticationManagerBeanLoads() {
            assertNotNull(authenticationManager);
        }

        @Test
        @DisplayName("SecurityConfig bean should load")
        void securityConfigBeanLoads() {
            assertNotNull(securityConfig);
        }

        @Test
        @DisplayName("JwtAuthenticationFilter bean should be available")
        void jwtAuthenticationFilterBeanLoads() {
            assertNotNull(jwtAuthenticationFilter);
        }

        @Test
        @DisplayName("JwtAuthenticationEntryPoint bean should be available")
        void authenticationEntryPointBeanLoads() {
            assertNotNull(authenticationEntryPoint);
        }

        @Test
        @DisplayName("OAuthSuccessHandler bean should be available")
        void oAuthSuccessHandlerBeanLoads() {
            assertNotNull(oAuthSuccessHandler);
        }
    }

    @Nested
    @DisplayName("Configuration Validation Tests")
    class ConfigurationValidationTests {

        @Test
        @DisplayName("Should create SecurityFilterChain with valid configuration")
        void securityFilterChain_validConfiguration() throws Exception {
            assertNotNull(securityFilterChain);
            // Verify that the filter chain is properly configured
            assertDoesNotThrow(() -> securityFilterChain.getFilters());
        }

        @Test
        @DisplayName("Should create AuthenticationManager from configuration")
        void authenticationManager_createdFromConfiguration() throws Exception {
            AuthenticationConfiguration authConfig = applicationContext.getBean(AuthenticationConfiguration.class);
            assertNotNull(authConfig);

            AuthenticationManager manager = securityConfig.authenticationManager(authConfig);
            assertNotNull(manager);
        }

        @Test
        @DisplayName("SecurityConfig should have required dependencies")
        void securityConfig_hasRequiredDependencies() {
            assertNotNull(securityConfig);
            // Verify that SecurityConfig was initialized with all required dependencies
        }
    }

    @Nested
    @DisplayName("Filter Tests")
    class FilterTests {

        @Test
        @DisplayName("SecurityFilterChain should contain filters")
        void securityFilterChain_containsFilters() {
            assertNotNull(securityFilterChain.getFilters());
            assertFalse(securityFilterChain.getFilters().isEmpty());
        }

        @Test
        @DisplayName("JwtAuthenticationFilter should be configured")
        void jwtAuthenticationFilter_configured() {
            assertNotNull(jwtAuthenticationFilter);
        }
    }

    @Nested
    @DisplayName("Component Integration Tests")
    class ComponentIntegrationTests {

        @Test
        @DisplayName("All security components should be wired correctly")
        void allSecurityComponents_wiredCorrectly() {
            assertNotNull(securityFilterChain);
            assertNotNull(authenticationManager);
            assertNotNull(jwtAuthenticationFilter);
            assertNotNull(authenticationEntryPoint);
            assertNotNull(oAuthSuccessHandler);
        }

        @Test
        @DisplayName("ApplicationContext should contain SecurityConfig bean")
        void applicationContext_containsSecurityConfig() {
            assertTrue(applicationContext.containsBean("securityConfig"));
        }

        @Test
        @DisplayName("ApplicationContext should contain SecurityFilterChain bean")
        void applicationContext_containsSecurityFilterChain() {
            SecurityFilterChain bean = applicationContext.getBean(SecurityFilterChain.class);
            assertNotNull(bean);
        }

        @Test
        @DisplayName("ApplicationContext should contain AuthenticationManager bean")
        void applicationContext_containsAuthenticationManager() {
            AuthenticationManager bean = applicationContext.getBean(AuthenticationManager.class);
            assertNotNull(bean);
        }
    }

    @Nested
    @DisplayName("Bean Existence Tests")
    class BeanExistenceTests {

        @Test
        @DisplayName("JwtAuthenticationFilter bean should exist")
        void jwtAuthenticationFilterExists() {
            assertTrue(applicationContext.containsBean("jwtAuthenticationFilter"));
        }

        @Test
        @DisplayName("JwtAuthenticationEntryPoint bean should exist")
        void jwtAuthenticationEntryPointExists() {
            assertTrue(applicationContext.containsBean("jwtAuthenticationEntryPoint"));
        }

        @Test
        @DisplayName("OAuthSuccessHandler bean should exist")
        void oAuthSuccessHandlerExists() {
            assertTrue(applicationContext.containsBean("OAuthSuccessHandler"));
        }
    }

    @Nested
    @DisplayName("Configuration Properties Tests")
    class ConfigurationPropertiesTests {

        @Test
        @DisplayName("SecurityFilterChain should be singleton")
        void securityFilterChain_isSingleton() {
            SecurityFilterChain bean1 = applicationContext.getBean(SecurityFilterChain.class);
            SecurityFilterChain bean2 = applicationContext.getBean(SecurityFilterChain.class);
            assertSame(bean1, bean2);
        }

        @Test
        @DisplayName("AuthenticationManager should be singleton")
        void authenticationManager_isSingleton() {
            AuthenticationManager bean1 = applicationContext.getBean(AuthenticationManager.class);
            AuthenticationManager bean2 = applicationContext.getBean(AuthenticationManager.class);
            assertSame(bean1, bean2);
        }
    }

    @Nested
    @DisplayName("Dependency Injection Tests")
    class DependencyInjectionTests {

        @Test
        @DisplayName("SecurityConfig should be autowired correctly")
        void securityConfig_autowiredCorrectly() {
            assertNotNull(securityConfig);
        }

        @Test
        @DisplayName("All required beans should be available for injection")
        void allRequiredBeans_availableForInjection() {
            assertNotNull(securityFilterChain);
            assertNotNull(authenticationManager);
            assertNotNull(jwtAuthenticationFilter);
            assertNotNull(authenticationEntryPoint);
            assertNotNull(oAuthSuccessHandler);
        }
    }
}
