package tech.zeta.Digital_Fixed_Deposit_System.entity.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Priyanshu Mishra
 */

public class RoleTest {

    @Test
    void fromString_parsesCaseInsensitive() {
        assertEquals(Role.ADMIN, Role.fromString("admin"));
        assertEquals(Role.USER, Role.fromString("USER"));
    }

    @Test
    void fromString_throwsOnInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> Role.fromString("invalid"));
    }
}
