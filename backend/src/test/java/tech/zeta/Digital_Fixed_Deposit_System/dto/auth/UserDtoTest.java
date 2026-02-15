package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;

import java.lang.reflect.Field;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Priyanshu Mishra
 */

public class UserDtoTest {

    @Test
    void changePasswordRequest_hasValidationAnnotations() throws Exception {
        Field current = ChangePasswordRequest.class.getDeclaredField("currentPassword");
        Field next = ChangePasswordRequest.class.getDeclaredField("newPassword");
        Field confirm = ChangePasswordRequest.class.getDeclaredField("confirmPassword");

        assertNotNull(current.getAnnotation(NotBlank.class));
        assertNotNull(next.getAnnotation(NotBlank.class));
        assertNotNull(confirm.getAnnotation(NotBlank.class));
    }

    @Test
    void changePasswordRequest_gettersReturnValues() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("old");
        request.setNewPassword("new");
        request.setConfirmPassword("new");

        assertEquals("old", request.getCurrentPassword());
        assertEquals("new", request.getNewPassword());
        assertEquals("new", request.getConfirmPassword());
    }

    @Test
    void updateUserProfileRequest_hasValidationAnnotations() throws Exception {
        Field name = UpdateUserProfileRequest.class.getDeclaredField("name");
        Field email = UpdateUserProfileRequest.class.getDeclaredField("email");

        assertNotNull(name.getAnnotation(NotBlank.class));
        assertNotNull(email.getAnnotation(NotBlank.class));
        assertNotNull(email.getAnnotation(Email.class));
    }

    @Test
    void updateUserProfileRequest_gettersReturnValues() {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");

        assertEquals("Alice", request.getName());
        assertEquals("alice@example.com", request.getEmail());
    }

    @Test
    void userProfileResponse_gettersReturnValues() {
        UserProfileResponse response = new UserProfileResponse(
                3L,
                "Alice",
                "alice@example.com",
                Role.USER,
                Instant.parse("2026-02-11T00:00:00Z")
        );

        assertEquals(3L, response.getId());
        assertEquals("Alice", response.getName());
        assertEquals("alice@example.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
        assertEquals(Instant.parse("2026-02-11T00:00:00Z"), response.getCreatedAt());
    }

    @Test
    void changePasswordRequest_toStringDoesNotExposePasswords() {
        ChangePasswordRequest request = new ChangePasswordRequest("secret", "newSecret", "newSecret");
        String str = request.toString();

        assertNotNull(str);
        assertEquals("ChangePasswordRequest{currentPassword=[PROTECTED], newPassword=[PROTECTED], confirmPassword=[PROTECTED]}", str);
    }

    @Test
    void updateUserProfileRequest_toStringReturnsValues() {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest("Bob", "bob@example.com");
        String str = request.toString();

        assertNotNull(str);
        assertEquals("UpdateUserProfileRequest{name='Bob', email='bob@example.com'}", str);
    }

    @Test
    void userProfileResponse_toStringReturnsValues() {
        UserProfileResponse response = new UserProfileResponse(
                5L,
                "Charlie",
                "charlie@example.com",
                Role.ADMIN,
                Instant.parse("2026-01-01T00:00:00Z")
        );
        String str = response.toString();

        assertNotNull(str);
        assertEquals("UserProfileResponse{id=5, name='Charlie', email='charlie@example.com', role=ADMIN, createdAt=2026-01-01T00:00:00Z}", str);
    }
}
