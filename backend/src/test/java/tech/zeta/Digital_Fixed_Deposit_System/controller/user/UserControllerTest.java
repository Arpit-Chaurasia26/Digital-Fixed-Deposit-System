package tech.zeta.Digital_Fixed_Deposit_System.controller.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.ChangePasswordRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UpdateUserProfileRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UserProfileResponse;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;
import tech.zeta.Digital_Fixed_Deposit_System.service.user.UserService;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Priyanshu Mishra
 */

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @Test
    void getCurrentUserProfile_returnsProfile() {
        UserProfileResponse profile = new UserProfileResponse(
                7L,
                "Jane Doe",
                "jane@example.com",
                Role.USER,
                Instant.parse("2026-02-11T00:00:00Z")
        );
        when(userService.getCurrentUserProfile()).thenReturn(profile);

        ResponseEntity<UserProfileResponse> response = controller.getCurrentUserProfile();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(profile, response.getBody());
        verify(userService).getCurrentUserProfile();
    }

    @Test
    void updateCurrentUserProfile_returnsUpdatedProfile() {
        UserProfileResponse profile = new UserProfileResponse(
                7L,
                "Updated",
                "updated@example.com",
                Role.USER,
                Instant.parse("2026-02-11T00:00:00Z")
        );
        UpdateUserProfileRequest request = new UpdateUserProfileRequest("Updated", "updated@example.com");
        when(userService.updateCurrentUserProfile(request)).thenReturn(profile);

        ResponseEntity<UserProfileResponse> response = controller.updateCurrentUserProfile(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(profile, response.getBody());
        verify(userService).updateCurrentUserProfile(request);
    }

    @Test
    void changePassword_returnsOkMessage() {
        ChangePasswordRequest request = new ChangePasswordRequest("old", "new", "new");

        ResponseEntity<String> response = controller.changePassword(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Password updated successfully", response.getBody());
        verify(userService).changePassword(request);
    }
}
