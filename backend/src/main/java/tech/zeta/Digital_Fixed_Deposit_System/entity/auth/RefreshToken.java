package tech.zeta.Digital_Fixed_Deposit_System.entity.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

/**
 * @author Priyanshu Mishra
 */



@Entity
@Getter
@Builder
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token_user", columnList = "user_id"),
                @Index(name = "idx_refresh_token_token", columnList = "token")
        }
)
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Owner of this refresh token

    @Column(name = "user_id", nullable = false)
    private Long userId;

    //Refresh token value (JWT or random string). Can be hashed later for extra security.

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    // Expiry time (e.g. now + 7 days)

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // Used to invalidate token on logout

    @Builder.Default
    @Column(nullable = false)
    private boolean revoked = false;

    //Audit field

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected RefreshToken() {
        // JPA only
    }

    public boolean isExpired() {
        boolean expired = Instant.now().isAfter(expiresAt);
        return expired;
    }
}
