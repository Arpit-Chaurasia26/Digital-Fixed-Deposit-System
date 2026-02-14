package tech.zeta.Digital_Fixed_Deposit_System.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.ChangePasswordRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UpdateUserProfileRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UserProfileResponse;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserService Unit Tests - Full Coverage")
class UserServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock private UserRepository userRepository;
    @Mock private CurrentUserProvider currentUserProvider;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static final Long USER_ID = 1L;
    private static final String EMAIL = "test@example.com";
    private static final String NAME = "Test User";
    private static final String PASSWORD = "encoded_password";

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(NAME, EMAIL, PASSWORD, Role.USER);
        setField(testUser, "id", USER_ID);
        setField(testUser, "createdAt", LocalDateTime.now());
        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
    }

    @Nested
    @DisplayName("Get Current User Profile Tests")
    class GetCurrentUserProfileTests {

        @Test
        @DisplayName("Should return user profile successfully")
        void getCurrentUserProfile_ShouldReturnProfile() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            UserProfileResponse response = userService.getCurrentUserProfile();

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(USER_ID);
            assertThat(response.getName()).isEqualTo(NAME);
            assertThat(response.getEmail()).isEqualTo(EMAIL);
            assertThat(response.getRole()).isEqualTo(Role.USER);
            logger.info("Get profile test passed");
        }

        @Test
        @DisplayName("Should throw when user not found")
        void getCurrentUserProfile_UserNotFound_ShouldThrow() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getCurrentUserProfile())
                    .isInstanceOf(UnauthorizedException.class);
            logger.info("User not found test passed");
        }
    }

    @Nested
    @DisplayName("Update User Profile Tests")
    class UpdateUserProfileTests {

        @Test
        @DisplayName("Should update profile successfully")
        void updateProfile_ValidRequest_ShouldSucceed() {
            UpdateUserProfileRequest request = createUpdateRequest("New Name", EMAIL);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            UserProfileResponse response = userService.updateCurrentUserProfile(request);

            assertThat(response).isNotNull();
            verify(userRepository).save(any(User.class));
            logger.info("Update profile test passed");
        }

        @Test
        @DisplayName("Should update email when new email is unique")
        void updateProfile_NewUniqueEmail_ShouldSucceed() {
            String newEmail = "new@example.com";
            UpdateUserProfileRequest request = createUpdateRequest(NAME, newEmail);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail(newEmail)).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            UserProfileResponse response = userService.updateCurrentUserProfile(request);

            assertThat(response).isNotNull();
            verify(userRepository).save(any(User.class));
            logger.info("Update email test passed");
        }

        @Test
        @DisplayName("Should reject duplicate email")
        void updateProfile_DuplicateEmail_ShouldThrow() {
            String existingEmail = "existing@example.com";
            UpdateUserProfileRequest request = createUpdateRequest(NAME, existingEmail);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

            assertThatThrownBy(() -> userService.updateCurrentUserProfile(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("already in use");
            logger.info("Duplicate email test passed");
        }

        @Test
        @DisplayName("Should throw when user not found")
        void updateProfile_UserNotFound_ShouldThrow() {
            UpdateUserProfileRequest request = createUpdateRequest(NAME, EMAIL);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateCurrentUserProfile(request))
                    .isInstanceOf(UnauthorizedException.class);
            logger.info("User not found update test passed");
        }
    }

    @Nested
    @DisplayName("Change Password Tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should change password successfully")
        void changePassword_ValidRequest_ShouldSucceed() {
            ChangePasswordRequest request = createChangePasswordRequest("oldPass", "newPass", "newPass");
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("oldPass", PASSWORD)).thenReturn(true);
            when(passwordEncoder.encode("newPass")).thenReturn("encoded_new_pass");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            userService.changePassword(request);

            verify(userRepository).save(any(User.class));
            verify(passwordEncoder).encode("newPass");
            logger.info("Change password test passed");
        }

        @Test
        @DisplayName("Should reject when passwords don't match")
        void changePassword_PasswordsMismatch_ShouldThrow() {
            ChangePasswordRequest request = createChangePasswordRequest("oldPass", "newPass", "differentPass");

            assertThatThrownBy(() -> userService.changePassword(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("do not match");
            logger.info("Passwords mismatch test passed");
        }

        @Test
        @DisplayName("Should reject incorrect current password")
        void changePassword_WrongCurrentPassword_ShouldThrow() {
            ChangePasswordRequest request = createChangePasswordRequest("wrongPass", "newPass", "newPass");
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongPass", PASSWORD)).thenReturn(false);

            assertThatThrownBy(() -> userService.changePassword(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("incorrect");
            logger.info("Wrong current password test passed");
        }

        @Test
        @DisplayName("Should throw when user not found")
        void changePassword_UserNotFound_ShouldThrow() {
            ChangePasswordRequest request = createChangePasswordRequest("oldPass", "newPass", "newPass");
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.changePassword(request))
                    .isInstanceOf(UnauthorizedException.class);
            logger.info("User not found password test passed");
        }
    }

    // Helper methods
    private UpdateUserProfileRequest createUpdateRequest(String name, String email) {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        setField(request, "name", name);
        setField(request, "email", email);
        return request;
    }

    private ChangePasswordRequest createChangePasswordRequest(String current, String newPass, String confirm) {
        ChangePasswordRequest request = new ChangePasswordRequest();
        setField(request, "currentPassword", current);
        setField(request, "newPassword", newPass);
        setField(request, "confirmPassword", confirm);
        return request;
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) { }
    }
}

