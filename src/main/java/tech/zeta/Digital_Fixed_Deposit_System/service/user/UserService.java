package tech.zeta.Digital_Fixed_Deposit_System.service.user;

import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UserProfileResponse;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    public UserService(
            UserRepository userRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.userRepository = userRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {

        Long userId = currentUserProvider.getCurrentUserId();

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
}
