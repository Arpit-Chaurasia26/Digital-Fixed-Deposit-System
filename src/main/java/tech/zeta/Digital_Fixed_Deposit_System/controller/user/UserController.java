package tech.zeta.Digital_Fixed_Deposit_System.controller.user;

import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UserProfileResponse;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UpdateUserProfileRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.ChangePasswordRequest;
import tech.zeta.Digital_Fixed_Deposit_System.service.user.IUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    // Get current logged-in user's profile
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        logger.info("User profile fetch requested");
        UserProfileResponse profile = userService.getCurrentUserProfile();
        logger.info("User profile fetch completed");
        return ResponseEntity.ok(profile);
    }

    // Update current logged-in user's profile
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        logger.info("User profile update requested");
        ResponseEntity<UserProfileResponse> response = ResponseEntity.ok(userService.updateCurrentUserProfile(request));
        logger.info("User profile update completed");
        return response;
    }

    // Change current logged-in user's password
    @PutMapping("/profile/password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        logger.info("Password change requested");
        userService.changePassword(request);
        logger.info("Password change completed");
        return ResponseEntity.ok("Password updated successfully");
    }
}
