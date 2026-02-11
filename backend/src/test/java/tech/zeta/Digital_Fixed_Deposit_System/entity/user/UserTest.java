package tech.zeta.Digital_Fixed_Deposit_System.entity.user;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
