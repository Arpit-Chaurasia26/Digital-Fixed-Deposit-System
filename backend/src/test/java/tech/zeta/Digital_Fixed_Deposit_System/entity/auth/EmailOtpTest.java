package tech.zeta.Digital_Fixed_Deposit_System.entity.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Priyanshu Mishra
 */

@DisplayName("EmailOtp Entity Tests")
class EmailOtpTest {

    @Test
    void gettersAndSetters_work() {
        EmailOtp otp = new EmailOtp();
        LocalDateTime expires = LocalDateTime.now().plusMinutes(5);

        otp.setId(1L);
        otp.setEmail("u@example.com");
        otp.setOtp("123456");
        otp.setExpiresAt(expires);
        otp.setVerified(true);

        assertThat(otp.getId()).isEqualTo(1L);
        assertThat(otp.getEmail()).isEqualTo("u@example.com");
        assertThat(otp.getOtp()).isEqualTo("123456");
        assertThat(otp.getExpiresAt()).isEqualTo(expires);
        assertThat(otp.isVerified()).isTrue();
    }
}
