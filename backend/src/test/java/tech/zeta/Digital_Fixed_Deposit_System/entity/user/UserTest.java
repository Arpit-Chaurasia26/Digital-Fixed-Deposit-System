package tech.zeta.Digital_Fixed_Deposit_System.entity.user;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
Author : Priyanshu Mishra
*/


public class UserTest {

    @Test
    void onCreate_setsDefaultsWhenMissing() {
        User user = new User("Name", "name@example.com", "hash", Role.ADMIN);
        user.setRole(null);
        ReflectionTestUtils.setField(user, "createdAt", null);

        ReflectionTestUtils.invokeMethod(user, "onCreate");

        assertNotNull(user.getCreatedAt());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void onCreate_preservesExistingValues() {
        User user = new User("Name", "name@example.com", "hash", Role.ADMIN);
        Instant created = Instant.now();
        ReflectionTestUtils.setField(user, "createdAt", created);

        ReflectionTestUtils.invokeMethod(user, "onCreate");

        assertEquals(created, user.getCreatedAt());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void settersAndGetters_coverMutableFields() {
        User user = new User("Name", "name@example.com", "hash", Role.USER);

        user.setName("Updated");
        user.setEmail("updated@example.com");
        user.setPassword("new-hash");
        user.setRole(Role.ADMIN);
        user.setFailedPasswordResetAttempts(3);
        user.setFailedLoginAttempts(5);
        user.setLoginBlockedUntil(java.time.LocalDateTime.now().plusMinutes(15));

        assertEquals("Updated", user.getName());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("new-hash", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
        assertEquals(3, user.getFailedPasswordResetAttempts());
        assertEquals(5, user.getFailedLoginAttempts());
        assertNotNull(user.getLoginBlockedUntil());
    }
}
