package tech.zeta.Digital_Fixed_Deposit_System.service.user;
 
import lombok.extern.slf4j.Slf4j;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UserProfileResponse;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UpdateUserProfileRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.ChangePasswordRequest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Priyanshu Mishra
 */


@Slf4j
@Service
public class UserService {


    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final PasswordEncoder passwordEncoder;
 
    public UserService(
            UserRepository userRepository,
            CurrentUserProvider currentUserProvider,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.currentUserProvider = currentUserProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean checkByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {

        Long userId = currentUserProvider.getCurrentUserId();
        log.info("Fetching current user profile: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UnauthorizedException("User not found")
                );

        return  UserProfileResponse
                .builder()
                .id( user.getId())
                .name( user.getName())
                .email(user.getEmail())
                .role( user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

    }

        @Transactional
        public UserProfileResponse updateCurrentUserProfile(UpdateUserProfileRequest request) {

                Long userId = currentUserProvider.getCurrentUserId();
                log.info("Updating user profile: userId={}", userId);

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UnauthorizedException("User not found"));

                String newEmail = request.getEmail().trim().toLowerCase();
                if (!newEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                        log.warn("Profile update rejected: email already in use: userId={}, email={}", userId, newEmail);
                        throw new BusinessException("Email is already in use");
                }
 
                user.setName(request.getName().trim());
                user.setEmail(newEmail);
 
                User saved = userRepository.save(user);
                log.info("User profile updated: userId={}", saved.getId());

            return  UserProfileResponse
                    .builder()
                    .id( user.getId())
                    .name( user.getName())
                    .email(user.getEmail())
                    .role( user.getRole())
                    .createdAt(user.getCreatedAt())
                    .build();
        }

        @Transactional
        public void changePassword(ChangePasswordRequest request) {

                if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                        throw new BusinessException("New password and confirm password do not match");
                }

                Long userId = currentUserProvider.getCurrentUserId();
                log.info("Changing password for userId={}", userId);

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UnauthorizedException("User not found"));

                if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        log.warn("Password change rejected: invalid current password for userId={}", userId);
                        throw new BusinessException("Current password is incorrect");
                }

                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
                log.info("Password changed for userId={}", userId);
        }
}
