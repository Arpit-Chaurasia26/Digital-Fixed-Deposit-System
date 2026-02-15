package tech.zeta.Digital_Fixed_Deposit_System.service.email;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.repository.EmailOtpRepository;

import java.time.LocalDateTime;

/*
Author : Priyanshu Mishra
*/


@Component
public class EmailOtpCleanupScheduler {

    private static final Logger logger =
            LogManager.getLogger(EmailOtpCleanupScheduler.class);

    private final EmailOtpRepository emailOtpRepository;

    public EmailOtpCleanupScheduler(
            EmailOtpRepository emailOtpRepository
    ) {
        this.emailOtpRepository = emailOtpRepository;
    }

    /**
     * Runs every 1 minute
     * Deletes expired OTPs automatically
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void cleanupExpiredOtps() {

        LocalDateTime now = LocalDateTime.now();

        logger.info("Running OTP cleanup job at {}", now);

        emailOtpRepository.deleteByExpiresAtBefore(now);

        logger.info("Expired OTP cleanup completed");
    }
}
