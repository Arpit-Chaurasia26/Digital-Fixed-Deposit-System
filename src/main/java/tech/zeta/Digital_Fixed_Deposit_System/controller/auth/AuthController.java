package tech.zeta.Digital_Fixed_Deposit_System.controller.auth;

import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.AuthResponse;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.LoginRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.RegisterRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UserProfileResponse;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.AuthService;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.TokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zeta.Digital_Fixed_Deposit_System.service.user.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final UserService userService;

    public AuthController(
            AuthService authService,
            TokenService tokenService,
            UserService userService
    ) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    // Register a new user.
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        User user = authService.register(request);
        String token = tokenService.generateToken(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponse(token));
    }

    // Login existing user.
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        User user = authService.login(request);
        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    // Fetch logged-in user's profile.
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

}
