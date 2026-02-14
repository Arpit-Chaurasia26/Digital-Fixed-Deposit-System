package tech.zeta.Digital_Fixed_Deposit_System.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InterestUtils Tests")
class InterestUtilsTest {

    @Test
    void calculateInterest_validInput_returnsRounded() {
        BigDecimal interest = InterestUtils.calculateInterest(
                new BigDecimal("10000.00"),
                new BigDecimal("12.00"),
                6L
        );

        assertThat(interest).isEqualByComparingTo("600.00");
    }

    @Test
    void calculateInterest_invalidInput_returnsZero() {
        assertThat(InterestUtils.calculateInterest(null, new BigDecimal("7.0"), 6L))
                .isEqualByComparingTo("0.00");
        assertThat(InterestUtils.calculateInterest(new BigDecimal("1000"), null, 6L))
                .isEqualByComparingTo("0.00");
        assertThat(InterestUtils.calculateInterest(new BigDecimal("1000"), new BigDecimal("7.0"), 0L))
                .isEqualByComparingTo("0.00");
    }
}

