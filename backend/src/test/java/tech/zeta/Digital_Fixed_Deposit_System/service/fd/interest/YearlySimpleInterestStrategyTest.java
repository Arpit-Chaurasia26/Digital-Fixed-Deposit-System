package tech.zeta.Digital_Fixed_Deposit_System.service.fd.interest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// Author - Arpit Chaurasia
@DisplayName("YearlySimpleInterestStrategy Unit Tests")
class YearlySimpleInterestStrategyTest {

    private final YearlySimpleInterestStrategy strategy = new YearlySimpleInterestStrategy();

    @Test
    @DisplayName("Supports YEARLY frequency")
    void supportsYearly() {
        assertThat(strategy.supports(InterestFrequency.YEARLY)).isTrue();
        assertThat(strategy.supports(InterestFrequency.MONTHLY)).isFalse();
    }

    @Test
    @DisplayName("Calculates interest for whole years")
    void calculate_ForWholeYears() {
        BigDecimal principal = new BigDecimal("1000.00");
        BigDecimal annualRate = new BigDecimal("0.10"); // 10%
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 1); // 2 years

        BigDecimal interest = strategy.calculate(principal, annualRate, start, end);

        // 1000 * 0.10 * 2 = 200
        assertThat(interest).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("Returns zero when end before start")
    void calculate_EndBeforeStart() {
        BigDecimal principal = new BigDecimal("1000.00");
        BigDecimal annualRate = new BigDecimal("0.10");
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 1);

        BigDecimal interest = strategy.calculate(principal, annualRate, start, end);

        assertThat(interest).isEqualByComparingTo(BigDecimal.ZERO);
    }
}

