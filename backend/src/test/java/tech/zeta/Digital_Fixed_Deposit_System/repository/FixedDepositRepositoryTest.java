package tech.zeta.Digital_Fixed_Deposit_System.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// Author - Arpit Chaurasia
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("FixedDepositRepository Tests")
class FixedDepositRepositoryTest {

    @Autowired
    private FixedDepositRepository repository;

    @BeforeEach
    void clearData() {
        repository.deleteAll();
    }

    @Test
    void findByUserId_returnsMatching() {
        repository.save(fd(1L, new BigDecimal("5000.00"), LocalDate.now().plusMonths(6)));
        repository.save(fd(2L, new BigDecimal("7000.00"), LocalDate.now().plusMonths(12)));

        assertThat(repository.findByUserId(1L)).hasSize(1);
    }

    @Test
    void findByUserIdAndAmountBetween_filters() {
        repository.save(fd(1L, new BigDecimal("4000.00"), LocalDate.now().plusMonths(6)));
        repository.save(fd(1L, new BigDecimal("8000.00"), LocalDate.now().plusMonths(6)));

        assertThat(repository.findByUserIdAndAmountBetween(1L, new BigDecimal("5000"), new BigDecimal("9000"))).hasSize(1);
    }

    @Test
    void findByIdAndUserId_returnsMatch() {
        FixedDeposit saved = repository.save(fd(3L, new BigDecimal("9000.00"), LocalDate.now().plusMonths(12)));

        assertThat(repository.findByIdAndUserId(saved.getId(), 3L)).isPresent();
    }

    private FixedDeposit fd(Long userId, BigDecimal amount, LocalDate maturity) {
        FixedDeposit fd = new FixedDeposit();
        fd.setUserId(userId);
        fd.setAmount(amount);
        fd.setInterestScheme(InterestScheme.STANDARD_6_MONTHS);
        fd.setInterestRate(new BigDecimal("5.50"));
        fd.setTenureMonths(6);
        fd.setStartDate(LocalDate.now());
        fd.setMaturityDate(maturity);
        fd.setStatus(FDStatus.ACTIVE);
        return fd;
    }
}
