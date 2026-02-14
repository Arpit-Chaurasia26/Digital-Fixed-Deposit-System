package tech.zeta.Digital_Fixed_Deposit_System.service.fd.interest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// Author - Arpit Chaurasia
@DisplayName("MonthlySimpleInterestStrategy Unit Tests")
class MonthlySimpleInterestStrategyTest {

    private final MonthlySimpleInterestStrategy strategy = new MonthlySimpleInterestStrategy();

    @Test
    @DisplayName("Supports MONTHLY frequency")
    void supportsMonthly() {
        assertThat(strategy.supports(InterestFrequency.MONTHLY)).isTrue();
        assertThat(strategy.supports(InterestFrequency.YEARLY)).isFalse();
    }

    @Test
    @DisplayName("Calculates interest for whole months")
    void calculate_ForWholeMonths() {
        BigDecimal principal = new BigDecimal("1000.00");
        BigDecimal annualRate = new BigDecimal("0.12"); // 12%
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 7, 1); // 6 months

        BigDecimal interest = strategy.calculate(principal, annualRate, start, end);

        // 1000 * (0.12/12) * 6 = 60
        assertThat(interest).isEqualByComparingTo(new BigDecimal("60.0000000000"));
    }

    @Test
    @DisplayName("Returns zero when end before start")
    void calculate_EndBeforeStart() {
        BigDecimal principal = new BigDecimal("1000.00");
        BigDecimal annualRate = new BigDecimal("0.12");
        LocalDate start = LocalDate.of(2025, 7, 1);
        LocalDate end = LocalDate.of(2025, 1, 1);

        BigDecimal interest = strategy.calculate(principal, annualRate, start, end);

        assertThat(interest).isEqualByComparingTo(BigDecimal.ZERO);
    }
}

