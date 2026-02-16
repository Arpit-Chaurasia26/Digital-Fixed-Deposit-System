package tech.zeta.Digital_Fixed_Deposit_System.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author Priyanshu Mishra
 */


@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email")
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Nullable → OAuth users don’t need passwords
    @Column(name = "password_hash")
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    // SECURITY & AUTH

    @Builder.Default
    @Column(nullable = false)
    private boolean emailVerified = false;

    @Builder.Default
    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    @Builder.Default
    @Column(nullable = false)
    private int failedPasswordResetAttempts = 0;

    @Builder.Default
    private LocalDateTime loginBlockedUntil = null;

    // OAUTH SUPPORT

    @Builder.Default
    @Column(length = 20)
    private String authProvider = "LOCAL"; // LOCAL / GOOGLE

    @Builder.Default
    @Column(length = 100)
    private String providerId = null; // Google sub ID

    //  CONSTRUCTORS

    // Custom constructor for backward compatibility
    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.authProvider = "LOCAL";
        this.emailVerified = false;
        this.failedLoginAttempts = 0;
        this.failedPasswordResetAttempts = 0;
    }

    //JPA HOOK

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (role == null) {
            role = Role.USER;
        }
    }

    //  GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public int getFailedPasswordResetAttempts() {
        return failedPasswordResetAttempts;
    }

    public void setFailedPasswordResetAttempts(int failedPasswordResetAttempts) {
        this.failedPasswordResetAttempts = failedPasswordResetAttempts;
    }

    public LocalDateTime getLoginBlockedUntil() {
        return loginBlockedUntil;
    }

    public void setLoginBlockedUntil(LocalDateTime loginBlockedUntil) {
        this.loginBlockedUntil = loginBlockedUntil;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
