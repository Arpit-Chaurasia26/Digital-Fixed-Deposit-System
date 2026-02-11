package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginRequestTest {

    @Test
    void gettersReturnAssignedValues() {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", "user@example.com");
        ReflectionTestUtils.setField(request, "password", "Password@123");

        assertEquals("user@example.com", request.getEmail());
        assertEquals("Password@123", request.getPassword());
    }
}
