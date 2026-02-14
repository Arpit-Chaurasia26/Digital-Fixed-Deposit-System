package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
Author : Priyanshu Mishra
*/


public class RegisterRequestTest {

    @Test
    void gettersReturnAssignedValues() {
        RegisterRequest request = new RegisterRequest();
        ReflectionTestUtils.setField(request, "name", "User");
        ReflectionTestUtils.setField(request, "email", "user@example.com");
        ReflectionTestUtils.setField(request, "password", "Password@123");

        assertEquals("User", request.getName());
        assertEquals("user@example.com", request.getEmail());
        assertEquals("Password@123", request.getPassword());
    }

    @Test
    void fieldsHaveValidationAnnotations() throws Exception {
        Field name = RegisterRequest.class.getDeclaredField("name");
        Field email = RegisterRequest.class.getDeclaredField("email");
        Field password = RegisterRequest.class.getDeclaredField("password");

        assertNotNull(name.getAnnotation(NotBlank.class));
        assertNotNull(email.getAnnotation(NotBlank.class));
        assertNotNull(email.getAnnotation(Email.class));
        assertNotNull(password.getAnnotation(NotBlank.class));
        assertNotNull(password.getAnnotation(Size.class));
        assertNotNull(password.getAnnotation(Pattern.class));
    }
}
