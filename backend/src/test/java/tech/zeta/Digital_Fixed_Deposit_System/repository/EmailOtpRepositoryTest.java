package tech.zeta.Digital_Fixed_Deposit_System.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.EmailOtp;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("EmailOtpRepository Tests")
class EmailOtpRepositoryTest {

    @Autowired
    private EmailOtpRepository repository;

    @BeforeEach
    void clearData() {
        repository.deleteAll();
    }

    @Test
    void findByEmailAndOtpAndVerifiedFalse_returnsMatch() {
        EmailOtp otp = otp("u@example.com", "123456", false, LocalDateTime.now().plusMinutes(5));
        repository.save(otp);

        assertThat(repository.findByEmailAndOtpAndVerifiedFalse("u@example.com", "123456")).isPresent();
    }

    @Test
    void findTopByEmailOrderByExpiresAtDesc_returnsLatest() {
        repository.save(otp("u@example.com", "old", false, LocalDateTime.now().plusMinutes(1)));
        repository.save(otp("u@example.com", "new", false, LocalDateTime.now().plusMinutes(10)));

        EmailOtp latest = repository.findTopByEmailOrderByExpiresAtDesc("u@example.com").orElseThrow();
        assertThat(latest.getOtp()).isEqualTo("new");
    }

    @Test
    void existsByEmailAndVerifiedTrue_reportsVerified() {
        repository.save(otp("v@example.com", "000000", true, LocalDateTime.now().plusMinutes(5)));

        assertThat(repository.existsByEmailAndVerifiedTrue("v@example.com")).isTrue();
    }

    @Test
    void deleteByEmail_removesRows() {
        repository.save(otp("d@example.com", "111111", false, LocalDateTime.now().plusMinutes(5)));

        repository.deleteByEmail("d@example.com");

        assertThat(repository.findTopByEmailOrderByExpiresAtDesc("d@example.com")).isEmpty();
    }

    private EmailOtp otp(String email, String code, boolean verified, LocalDateTime expires) {
        EmailOtp otp = new EmailOtp();
        otp.setEmail(email);
        otp.setOtp(code);
        otp.setVerified(verified);
        otp.setExpiresAt(expires);
        return otp;
    }
}
