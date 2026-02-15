package tech.zeta.Digital_Fixed_Deposit_System.service.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.zeta.Digital_Fixed_Deposit_System.repository.EmailOtpRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * @author Priyanshu Mishra
 */

@ExtendWith(MockitoExtension.class)
class EmailOtpCleanupSchedulerTest {

    @Mock
    private EmailOtpRepository emailOtpRepository;

    @InjectMocks
    private EmailOtpCleanupScheduler scheduler;

    @Test
    void cleanupExpiredOtps_deletesByTimestamp() {
        LocalDateTime beforeCall = LocalDateTime.now().minusSeconds(2);

        scheduler.cleanupExpiredOtps();

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(emailOtpRepository).deleteByExpiresAtBefore(captor.capture());

        LocalDateTime passed = captor.getValue();
        LocalDateTime afterCall = LocalDateTime.now().plusSeconds(2);

        assertThat(passed).isAfterOrEqualTo(beforeCall);
        assertThat(passed).isBeforeOrEqualTo(afterCall);
    }
}
