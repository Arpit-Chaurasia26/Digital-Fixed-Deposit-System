package tech.zeta.Digital_Fixed_Deposit_System.dto.auth;

import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.Instant;

/**
 * @author Priyanshu Mishra
 */


// Response DTO for authenticated user's profile.
public class UserProfileResponse {


    private final Long id;
    private final String name;
    private final String email;
    private final Role role;
    private final Instant createdAt;

    public UserProfileResponse(
            Long id,
            String name,
            String email,
            Role role,
            Instant createdAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "UserProfileResponse{id=" + id + ", name='" + name + "', email='" + email + "', role=" + role + ", createdAt=" + createdAt + "}";
    }
}
