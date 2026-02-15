package tech.zeta.Digital_Fixed_Deposit_System.controller.user;
 
import lombok.extern.slf4j.Slf4j;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UserProfileResponse;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UpdateUserProfileRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.ChangePasswordRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.zeta.Digital_Fixed_Deposit_System.service.user.UserService;

/**
 * @author Priyanshu Mishra
 */

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {


    private final UserService userService;
 
    public UserController(UserService userService) {
        this.userService = userService;
    }
 
    // Get current logged-in user's profile
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        log.info("User profile fetch requested");
        UserProfileResponse profile = userService.getCurrentUserProfile();
        log.info("User profile fetch completed");
        return ResponseEntity.ok(profile);
    }
 
    // Update current logged-in user's profile
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        log.info("User profile update requested");
        ResponseEntity<UserProfileResponse> response = ResponseEntity.ok(userService.updateCurrentUserProfile(request));
        log.info("User profile update completed");
        return response;
    }
 
    // Change current logged-in user's password
    @PutMapping("/profile/password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        log.info("Password change requested");
        userService.changePassword(request);
        log.info("Password change completed");
        return ResponseEntity.ok("Password updated successfully");
    }
}
