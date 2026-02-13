package tech.zeta.Digital_Fixed_Deposit_System.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.EmailOtp;

import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findByEmailAndOtpAndVerifiedFalse(String email, String otp);
    Optional<EmailOtp> findTopByEmailOrderByExpiresAtDesc(String email);

    boolean existsByEmailAndVerifiedTrue(String email);

    void deleteByEmail(String email);
}
