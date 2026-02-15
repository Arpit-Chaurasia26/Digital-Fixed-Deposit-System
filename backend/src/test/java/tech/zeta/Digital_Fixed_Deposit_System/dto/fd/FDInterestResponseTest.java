package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arpit Chaurasia
 */
@DisplayName("FDInterestResponse DTO Tests")
class FDInterestResponseTest {

    @Test
    void constructor_setsFields() {
        LocalDate start = LocalDate.now().minusMonths(3);
        LocalDate maturity = LocalDate.now().plusMonths(9);

        FDInterestResponse dto = new FDInterestResponse(
                10L,
                FDStatus.ACTIVE,
                new BigDecimal("10000.00"),
                new BigDecimal("250.00"),
                new BigDecimal("6.50"),
                InterestFrequency.MONTHLY,
                start,
                maturity
        );

        assertThat(dto.getFdId()).isEqualTo(10L);
        assertThat(dto.getStatus()).isEqualTo(FDStatus.ACTIVE);
        assertThat(dto.getAmount()).isEqualByComparingTo("10000.00");
        assertThat(dto.getAccruedInterest()).isEqualByComparingTo("250.00");
        assertThat(dto.getInterestRate()).isEqualByComparingTo("6.50");
        assertThat(dto.getInterestFrequency()).isEqualTo(InterestFrequency.MONTHLY);
        assertThat(dto.getStartDate()).isEqualTo(start);
        assertThat(dto.getMaturityDate()).isEqualTo(maturity);
    }
}

