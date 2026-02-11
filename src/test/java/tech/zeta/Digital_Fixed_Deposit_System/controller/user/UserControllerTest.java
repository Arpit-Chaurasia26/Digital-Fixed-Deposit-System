package tech.zeta.Digital_Fixed_Deposit_System.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.ChangePasswordRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UpdateUserProfileRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UserProfileResponse;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;
import tech.zeta.Digital_Fixed_Deposit_System.service.user.IUserService;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserControllerTest {

    @Test
    void getCurrentUserProfile_returnsProfile() {
        UserProfileResponse profile = new UserProfileResponse(
                7L,
                "Jane Doe",
                "jane@example.com",
                Role.USER,
                Instant.parse("2026-02-11T00:00:00Z")
        );
        UserServiceStub service = new UserServiceStub(profile);
        UserController controller = new UserController(service);

        ResponseEntity<UserProfileResponse> response = controller.getCurrentUserProfile();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(profile, response.getBody());
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
        UserServiceStub service = new UserServiceStub(profile);
        UserController controller = new UserController(service);

        UpdateUserProfileRequest request = new UpdateUserProfileRequest("Updated", "updated@example.com");
        ResponseEntity<UserProfileResponse> response = controller.updateCurrentUserProfile(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Updated", service.lastUpdateRequest.getName());
        assertEquals("updated@example.com", service.lastUpdateRequest.getEmail());
        assertEquals(profile, response.getBody());
    }

    @Test
    void changePassword_returnsOkMessage() {
        UserServiceStub service = new UserServiceStub(null);
        UserController controller = new UserController(service);

        ChangePasswordRequest request = new ChangePasswordRequest("old", "new", "new");
        ResponseEntity<String> response = controller.changePassword(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Password updated successfully", response.getBody());
        assertNotNull(service.lastChangePasswordRequest);
    }

    private static class UserServiceStub implements IUserService {
        private final UserProfileResponse profileResponse;
        private UpdateUserProfileRequest lastUpdateRequest;
        private ChangePasswordRequest lastChangePasswordRequest;

        private UserServiceStub(UserProfileResponse profileResponse) {
            this.profileResponse = profileResponse;
        }

        @Override
        public UserProfileResponse getCurrentUserProfile() {
            return profileResponse;
        }

        @Override
        public UserProfileResponse updateCurrentUserProfile(UpdateUserProfileRequest request) {
            this.lastUpdateRequest = request;
            return profileResponse;
        }

        @Override
        public void changePassword(ChangePasswordRequest request) {
            this.lastChangePasswordRequest = request;
        }
    }
}

