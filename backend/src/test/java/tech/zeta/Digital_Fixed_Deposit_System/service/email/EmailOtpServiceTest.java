package tech.zeta.Digital_Fixed_Deposit_System.service.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.EmailOtp;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.EmailOtpRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/*
Author : Priyanshu Mishra
*/

/**
 * @author Priyanshu Mishra
 */

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("EmailOtpService Unit Tests - Full Coverage")
class EmailOtpServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(EmailOtpServiceTest.class);

    @Mock private EmailOtpRepository emailOtpRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private EmailOtpService emailOtpService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_OTP = "123456";

    @Nested
    @DisplayName("Send OTP Tests")
    class SendOtpTests {

        @Test
        @DisplayName("Should send OTP successfully")
        void sendOtp_ShouldSucceed() {
            when(emailOtpRepository.save(any(EmailOtp.class))).thenAnswer(inv -> inv.getArgument(0));
            doNothing().when(emailService).sendOtpEmail(anyString(), anyString());

            emailOtpService.sendOtp(TEST_EMAIL);

            ArgumentCaptor<EmailOtp> captor = ArgumentCaptor.forClass(EmailOtp.class);
            verify(emailOtpRepository).save(captor.capture());
            verify(emailService).sendOtpEmail(eq(TEST_EMAIL), anyString());

            EmailOtp savedOtp = captor.getValue();
            assertThat(savedOtp.getEmail()).isEqualTo(TEST_EMAIL);
            assertThat(savedOtp.getOtp()).hasSize(6);
            assertThat(savedOtp.isVerified()).isFalse();
            assertThat(savedOtp.getExpiresAt()).isAfter(LocalDateTime.now());
            logger.info("Send OTP test passed");
        }

        @Test
        @DisplayName("Should generate 6-digit OTP")
        void sendOtp_ShouldGenerate6DigitOtp() {
            ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);
            when(emailOtpRepository.save(any(EmailOtp.class))).thenAnswer(inv -> inv.getArgument(0));
            doNothing().when(emailService).sendOtpEmail(anyString(), otpCaptor.capture());

            emailOtpService.sendOtp(TEST_EMAIL);

            String otp = otpCaptor.getValue();
            assertThat(otp).matches("\\d{6}");
            int otpValue = Integer.parseInt(otp);
            assertThat(otpValue).isBetween(100000, 999999);
        }

        @Test
        @DisplayName("Should set expiration time 5 minutes in future")
        void sendOtp_ShouldSetCorrectExpiration() {
            ArgumentCaptor<EmailOtp> captor = ArgumentCaptor.forClass(EmailOtp.class);
            when(emailOtpRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
            doNothing().when(emailService).sendOtpEmail(anyString(), anyString());

            LocalDateTime beforeSend = LocalDateTime.now();
            emailOtpService.sendOtp(TEST_EMAIL);
            LocalDateTime afterSend = LocalDateTime.now();

            EmailOtp savedOtp = captor.getValue();
            assertThat(savedOtp.getExpiresAt()).isAfter(beforeSend.plusMinutes(4));
            assertThat(savedOtp.getExpiresAt()).isBefore(afterSend.plusMinutes(6));
        }
    }

    @Nested
    @DisplayName("Verify OTP Tests")
    class VerifyOtpTests {

        @Test
        @DisplayName("Should verify valid OTP")
        void verifyOtp_ValidOtp_ShouldSucceed() {
            EmailOtp emailOtp = createEmailOtp(false, LocalDateTime.now().plusMinutes(5));
            when(emailOtpRepository.findByEmailAndOtpAndVerifiedFalse(TEST_EMAIL, TEST_OTP))
                    .thenReturn(Optional.of(emailOtp));
            when(emailOtpRepository.save(any(EmailOtp.class))).thenReturn(emailOtp);

            emailOtpService.verifyOtp(TEST_EMAIL, TEST_OTP);

            assertThat(emailOtp.isVerified()).isTrue();
            verify(emailOtpRepository).save(emailOtp);
        }

        @Test
        @DisplayName("Should reject invalid OTP")
        void verifyOtp_InvalidOtp_ShouldThrow() {
            when(emailOtpRepository.findByEmailAndOtpAndVerifiedFalse(TEST_EMAIL, "wrong"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> emailOtpService.verifyOtp(TEST_EMAIL, "wrong"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Invalid OTP");
        }

        @Test
        @DisplayName("Should reject expired OTP")
        void verifyOtp_ExpiredOtp_ShouldThrow() {
            EmailOtp expiredOtp = createEmailOtp(false, LocalDateTime.now().minusMinutes(1));
            when(emailOtpRepository.findByEmailAndOtpAndVerifiedFalse(TEST_EMAIL, TEST_OTP))
                    .thenReturn(Optional.of(expiredOtp));

            assertThatThrownBy(() -> emailOtpService.verifyOtp(TEST_EMAIL, TEST_OTP))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("OTP expired");
        }
    }

    private EmailOtp createEmailOtp(boolean verified, LocalDateTime expiresAt) {
        EmailOtp otp = new EmailOtp();
        otp.setEmail(TEST_EMAIL);
        otp.setOtp(TEST_OTP);
        otp.setVerified(verified);
        otp.setExpiresAt(expiresAt);
        return otp;
    }
}
