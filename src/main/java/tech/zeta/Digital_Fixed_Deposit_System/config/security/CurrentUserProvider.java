package tech.zeta.Digital_Fixed_Deposit_System.config.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;

@Component
public class CurrentUserProvider {

    private static final Logger logger = LogManager.getLogger(CurrentUserProvider.class);

    // Returns the authenticated user's ID from the security context.
    public Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Authentication missing or not authenticated");
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Long)) {
            logger.warn("Authentication principal type invalid: {}", principal == null ? "null" : principal.getClass().getName());
            throw new UnauthorizedException("Invalid authentication principal");
        }

        return (Long) principal;
    }
}
