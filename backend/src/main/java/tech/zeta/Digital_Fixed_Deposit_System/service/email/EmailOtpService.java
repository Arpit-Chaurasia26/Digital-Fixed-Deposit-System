package tech.zeta.Digital_Fixed_Deposit_System.service.email;

import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.EmailOtp;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.EmailOtpRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * @author Priyanshu Mishra
 */

@Slf4j
@Service
public class EmailOtpService {

    private final EmailOtpRepository emailOtpRepository;
    private final EmailService emailService;

    public EmailOtpService(
            EmailOtpRepository emailOtpRepository,
            EmailService emailService
    ) {
        this.emailOtpRepository = emailOtpRepository;
        this.emailService = emailService;
    }

    // SEND OTP
    public void sendOtp(String email) {

        log.info("Generating OTP for email: {}", email);

        String otp =
                String.valueOf(100000 + new SecureRandom().nextInt(900000));

        EmailOtp entity =EmailOtp
                .builder()
                .email(email)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();
        entity.setVerified(false);

        emailOtpRepository.save(entity);

        log.info("OTP saved to database for email: {}", email);

        emailService.sendOtpEmail(email, otp);

        log.info("OTP email sent successfully to: {}", email);
    }

    // VERIFY OTP
    public void verifyOtp(String email, String otp) {

        log.info("Verifying OTP for email: {}", email);

        EmailOtp record = emailOtpRepository
                .findByEmailAndOtpAndVerifiedFalse(email, otp)
                .orElseThrow(() -> {
                    log.warn("Invalid OTP attempt for email: {}", email);
                    return new BusinessException("Invalid OTP");
                });

        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Expired OTP attempt for email: {}", email);
            throw new BusinessException("OTP expired");
        }

        record.setVerified(true);
        emailOtpRepository.save(record);

        log.info("OTP verified successfully for email: {}", email);
    }
}
