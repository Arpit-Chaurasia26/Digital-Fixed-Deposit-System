package tech.zeta.Digital_Fixed_Deposit_System.service.user;
 
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

@Service
public class UserService implements IUserService {
 
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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
 
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {

        Long userId = currentUserProvider.getCurrentUserId();
        logger.info("Fetching current user profile: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UnauthorizedException("User not found")
                );

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
 
        @Override
        @Transactional
        public UserProfileResponse updateCurrentUserProfile(UpdateUserProfileRequest request) {

                Long userId = currentUserProvider.getCurrentUserId();
                logger.info("Updating user profile: userId={}", userId);

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UnauthorizedException("User not found"));

                String newEmail = request.getEmail().trim().toLowerCase();
                if (!newEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                        logger.warn("Profile update rejected: email already in use: userId={}, email={}", userId, newEmail);
                        throw new BusinessException("Email is already in use");
                }
 
                user.setName(request.getName().trim());
                user.setEmail(newEmail);
 
                User saved = userRepository.save(user);
                logger.info("User profile updated: userId={}", saved.getId());

                return new UserProfileResponse(
                                saved.getId(),
                                saved.getName(),
                                saved.getEmail(),
                                saved.getRole(),
                                saved.getCreatedAt()
                );
        }
 
        @Override
        @Transactional
        public void changePassword(ChangePasswordRequest request) {

                if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                        throw new BusinessException("New password and confirm password do not match");
                }

                Long userId = currentUserProvider.getCurrentUserId();
                logger.info("Changing password for userId={}", userId);

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UnauthorizedException("User not found"));

                if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        logger.warn("Password change rejected: invalid current password for userId={}", userId);
                        throw new BusinessException("Current password is incorrect");
                }

                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
                logger.info("Password changed for userId={}", userId);
        }
}
