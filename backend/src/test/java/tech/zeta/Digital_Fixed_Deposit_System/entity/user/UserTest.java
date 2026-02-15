package tech.zeta.Digital_Fixed_Deposit_System.entity.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Priyanshu Mishra
 */

@DisplayName("User Entity Tests")
public class UserTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create user with valid parameters")
        void constructor_validParameters_createsUser() {
            User user = new User("John Doe", "john@example.com", "hashedPassword", Role.USER);

            assertEquals("John Doe", user.getName());
            assertEquals("john@example.com", user.getEmail());
            assertEquals("hashedPassword", user.getPassword());
            assertEquals(Role.USER, user.getRole());
            assertEquals("LOCAL", user.getAuthProvider());
        }

        @Test
        @DisplayName("Should create admin user")
        void constructor_adminRole_createsAdminUser() {
            User user = new User("Admin", "admin@example.com", "hash", Role.ADMIN);

            assertEquals(Role.ADMIN, user.getRole());
            assertEquals("LOCAL", user.getAuthProvider());
        }

        @Test
        @DisplayName("Should set LOCAL as default auth provider")
        void constructor_setsLocalAuthProvider() {
            User user = new User("User", "user@example.com", "pass", Role.USER);

            assertEquals("LOCAL", user.getAuthProvider());
        }

        @Test
        @DisplayName("Protected constructor should work")
        void protectedConstructor_createsEmptyUser() {
            assertDoesNotThrow(() -> {
                User user = User.class.getDeclaredConstructor().newInstance();
                assertNull(user.getName());
            });
        }
    }

    @Nested
    @DisplayName("JPA Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onCreate should set defaults when missing")
        void onCreate_setsDefaultsWhenMissing() {
            User user = new User("Name", "name@example.com", "hash", Role.ADMIN);
            user.setRole(null);
            ReflectionTestUtils.setField(user, "createdAt", null);

            ReflectionTestUtils.invokeMethod(user, "onCreate");

            assertNotNull(user.getCreatedAt());
            assertEquals(Role.USER, user.getRole());
        }

        @Test
        @DisplayName("onCreate should preserve existing values")
        void onCreate_preservesExistingValues() {
            User user = new User("Name", "name@example.com", "hash", Role.ADMIN);
            Instant created = Instant.now();
            ReflectionTestUtils.setField(user, "createdAt", created);

            ReflectionTestUtils.invokeMethod(user, "onCreate");

            assertEquals(created, user.getCreatedAt());
            assertEquals(Role.ADMIN, user.getRole());
        }

        @Test
        @DisplayName("onCreate should set USER role when null")
        void onCreate_nullRole_setsUserRole() {
            User user = new User("Name", "name@example.com", "hash", Role.USER);
            user.setRole(null);

            ReflectionTestUtils.invokeMethod(user, "onCreate");

            assertEquals(Role.USER, user.getRole());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set name")
        void nameGetterSetter() {
            User user = new User("Original", "email@example.com", "pass", Role.USER);

            user.setName("Updated Name");

            assertEquals("Updated Name", user.getName());
        }

        @Test
        @DisplayName("Should get and set email")
        void emailGetterSetter() {
            User user = new User("Name", "original@example.com", "pass", Role.USER);

            user.setEmail("updated@example.com");

            assertEquals("updated@example.com", user.getEmail());
        }

        @Test
        @DisplayName("Should get and set password")
        void passwordGetterSetter() {
            User user = new User("Name", "email@example.com", "original-hash", Role.USER);

            user.setPassword("new-hash");

            assertEquals("new-hash", user.getPassword());
        }

        @Test
        @DisplayName("Should get and set role")
        void roleGetterSetter() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);

            user.setRole(Role.ADMIN);

            assertEquals(Role.ADMIN, user.getRole());
        }

        @Test
        @DisplayName("Should get ID")
        void idGetter() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);

            assertNull(user.getId()); // ID is null before persistence
        }

        @Test
        @DisplayName("Should get createdAt")
        void createdAtGetter() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);
            Instant now = Instant.now();
            ReflectionTestUtils.setField(user, "createdAt", now);

            assertEquals(now, user.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Security Field Tests")
    class SecurityFieldTests {

        @Test
        @DisplayName("Should get and set emailVerified")
        void emailVerifiedGetterSetter() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);

            assertFalse(user.isEmailVerified()); // default

            user.setEmailVerified(true);

            assertTrue(user.isEmailVerified());
        }

        @Test
        @DisplayName("Should get and set failedLoginAttempts")
        void failedLoginAttemptsGetterSetter() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);

            assertEquals(0, user.getFailedLoginAttempts()); // default

            user.setFailedLoginAttempts(5);

            assertEquals(5, user.getFailedLoginAttempts());
        }

        @Test
        @DisplayName("Should get and set failedPasswordResetAttempts")
        void failedPasswordResetAttemptsGetterSetter() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);

            assertEquals(0, user.getFailedPasswordResetAttempts()); // default

            user.setFailedPasswordResetAttempts(3);

            assertEquals(3, user.getFailedPasswordResetAttempts());
        }

        @Test
        @DisplayName("Should get and set loginBlockedUntil")
        void loginBlockedUntilGetterSetter() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);

            assertNull(user.getLoginBlockedUntil()); // default

            LocalDateTime blockedUntil = LocalDateTime.now().plusMinutes(15);
            user.setLoginBlockedUntil(blockedUntil);

            assertEquals(blockedUntil, user.getLoginBlockedUntil());
        }
    }

    @Nested
    @DisplayName("OAuth Field Tests")
    class OAuthFieldTests {

        @Test
        @DisplayName("Should get and set authProvider")
        void authProviderGetterSetter() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);

            assertEquals("LOCAL", user.getAuthProvider()); // default from constructor

            user.setAuthProvider("GOOGLE");

            assertEquals("GOOGLE", user.getAuthProvider());
        }

        @Test
        @DisplayName("Should get and set providerId")
        void providerIdGetterSetter() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);

            assertNull(user.getProviderId()); // default

            user.setProviderId("google-sub-12345");

            assertEquals("google-sub-12345", user.getProviderId());
        }

        @Test
        @DisplayName("Should handle OAuth user creation")
        void oauthUserScenario() {
            User user = new User("OAuth User", "oauth@example.com", "OAUTH_USER", Role.USER);
            user.setAuthProvider("GOOGLE");
            user.setProviderId("google-12345");
            user.setEmailVerified(true);

            assertEquals("GOOGLE", user.getAuthProvider());
            assertEquals("google-12345", user.getProviderId());
            assertEquals("OAUTH_USER", user.getPassword());
            assertTrue(user.isEmailVerified());
        }
    }

    @Nested
    @DisplayName("Complete Field Coverage Tests")
    class CompleteFieldCoverageTests {

        @Test
        @DisplayName("Should cover all mutable fields")
        void settersAndGetters_coverAllMutableFields() {
            User user = new User("Name", "name@example.com", "hash", Role.USER);

            // Set all mutable fields
            user.setName("Updated");
            user.setEmail("updated@example.com");
            user.setPassword("new-hash");
            user.setRole(Role.ADMIN);
            user.setFailedPasswordResetAttempts(3);
            user.setFailedLoginAttempts(5);
            LocalDateTime blockedUntil = LocalDateTime.now().plusMinutes(15);
            user.setLoginBlockedUntil(blockedUntil);
            user.setEmailVerified(true);
            user.setAuthProvider("GOOGLE");
            user.setProviderId("provider-123");

            // Verify all fields
            assertEquals("Updated", user.getName());
            assertEquals("updated@example.com", user.getEmail());
            assertEquals("new-hash", user.getPassword());
            assertEquals(Role.ADMIN, user.getRole());
            assertEquals(3, user.getFailedPasswordResetAttempts());
            assertEquals(5, user.getFailedLoginAttempts());
            assertEquals(blockedUntil, user.getLoginBlockedUntil());
            assertTrue(user.isEmailVerified());
            assertEquals("GOOGLE", user.getAuthProvider());
            assertEquals("provider-123", user.getProviderId());
        }

        @Test
        @DisplayName("Should handle boundary values for attempts")
        void boundaryValuesForAttempts() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);

            user.setFailedLoginAttempts(0);
            user.setFailedPasswordResetAttempts(0);
            assertEquals(0, user.getFailedLoginAttempts());
            assertEquals(0, user.getFailedPasswordResetAttempts());

            user.setFailedLoginAttempts(100);
            user.setFailedPasswordResetAttempts(100);
            assertEquals(100, user.getFailedLoginAttempts());
            assertEquals(100, user.getFailedPasswordResetAttempts());
        }

        @Test
        @DisplayName("Should handle null values gracefully")
        void nullValueHandling() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);

            user.setName(null);
            user.setEmail(null);
            user.setPassword(null);
            user.setLoginBlockedUntil(null);
            user.setAuthProvider(null);
            user.setProviderId(null);

            assertNull(user.getName());
            assertNull(user.getEmail());
            assertNull(user.getPassword());
            assertNull(user.getLoginBlockedUntil());
            assertNull(user.getAuthProvider());
            assertNull(user.getProviderId());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty strings")
        void emptyStringHandling() {
            User user = new User("", "", "", Role.USER);

            assertEquals("", user.getName());
            assertEquals("", user.getEmail());
            assertEquals("", user.getPassword());
        }

        @Test
        @DisplayName("Should handle very long strings")
        void longStringHandling() {
            String longString = "a".repeat(200);
            User user = new User(longString, longString + "@example.com", longString, Role.USER);

            assertTrue(user.getName().length() > 0);
            assertTrue(user.getEmail().length() > 0);
        }

        @Test
        @DisplayName("Should handle past loginBlockedUntil")
        void pastLoginBlockedUntil() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);
            LocalDateTime pastTime = LocalDateTime.now().minusHours(1);

            user.setLoginBlockedUntil(pastTime);

            assertEquals(pastTime, user.getLoginBlockedUntil());
        }

        @Test
        @DisplayName("Should handle future loginBlockedUntil")
        void futureLoginBlockedUntil() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);
            LocalDateTime futureTime = LocalDateTime.now().plusDays(30);

            user.setLoginBlockedUntil(futureTime);

            assertEquals(futureTime, user.getLoginBlockedUntil());
        }

        @Test
        @DisplayName("Should toggle emailVerified multiple times")
        void emailVerifiedToggle() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);

            user.setEmailVerified(true);
            assertTrue(user.isEmailVerified());

            user.setEmailVerified(false);
            assertFalse(user.isEmailVerified());

            user.setEmailVerified(true);
            assertTrue(user.isEmailVerified());
        }

        @Test
        @DisplayName("Should handle negative attempt values")
        void negativeAttempts() {
            User user = new User("Name", "email@example.com", "pass", Role.USER);

            user.setFailedLoginAttempts(-1);
            user.setFailedPasswordResetAttempts(-5);

            assertEquals(-1, user.getFailedLoginAttempts());
            assertEquals(-5, user.getFailedPasswordResetAttempts());
        }
    }
}
