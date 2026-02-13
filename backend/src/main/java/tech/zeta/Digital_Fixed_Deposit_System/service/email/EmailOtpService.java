package tech.zeta.Digital_Fixed_Deposit_System.service.email;

import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.EmailOtp;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.EmailOtpRepository;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

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

        String otp =
                String.valueOf(100000 + new SecureRandom().nextInt(900000));

        EmailOtp entity = new EmailOtp();
        entity.setEmail(email);
        entity.setOtp(otp);
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        entity.setVerified(false);

        emailOtpRepository.save(entity);
        emailService.sendOtpEmail(email, otp);
    }

    // VERIFY OTP
    public void verifyOtp(String email, String otp) {

        EmailOtp record = emailOtpRepository
                .findByEmailAndOtpAndVerifiedFalse(email, otp)
                .orElseThrow(() ->
                        new BusinessException("Invalid OTP"));

        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("OTP expired");
        }

        record.setVerified(true);
        emailOtpRepository.save(record);
    }
}
