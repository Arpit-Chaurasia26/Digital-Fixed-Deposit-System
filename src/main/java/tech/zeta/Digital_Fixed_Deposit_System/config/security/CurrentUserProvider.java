package tech.zeta.Digital_Fixed_Deposit_System.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;

@Component
public class CurrentUserProvider {

    // Returns the authenticated user's ID from the security context.
    public Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Long)) {
            throw new UnauthorizedException("Invalid authentication principal");
        }

        return (Long) principal;
    }
}
