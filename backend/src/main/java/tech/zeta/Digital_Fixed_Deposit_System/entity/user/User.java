package tech.zeta.Digital_Fixed_Deposit_System.entity.user;

import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;



    protected User() {
    }

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // JPA Lifecycle Hooks

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (role == null) {
            role = Role.USER;
        }
    }

    @Column(nullable = false)
    private int failedPasswordResetAttempts = 0;

    private LocalDateTime loginBlockedUntil;


    @Column(nullable = false)
    private int failedLoginAttempts = 0;



    // Getters & Setters

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

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int attempts) {
        this.failedLoginAttempts = attempts;
    }
}
