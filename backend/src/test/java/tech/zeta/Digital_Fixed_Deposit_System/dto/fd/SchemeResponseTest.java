package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arpit Chaurasia
 */
@DisplayName("SchemeResponse DTO Tests")
class SchemeResponseTest {

    @Test
    void record_accessors_work() {
        SchemeResponse dto = new SchemeResponse(
                "STANDARD_6_MONTHS",
                new BigDecimal("5.50"),
                6,
                "MONTHLY",
                true
        );

        assertThat(dto.name()).isEqualTo("STANDARD_6_MONTHS");
        assertThat(dto.annualInterestRate()).isEqualByComparingTo("5.50");
        assertThat(dto.tenureInMonths()).isEqualTo(6);
        assertThat(dto.interestFrequency()).isEqualTo("MONTHLY");
        assertThat(dto.prematureBreakAllowed()).isTrue();
    }
}

