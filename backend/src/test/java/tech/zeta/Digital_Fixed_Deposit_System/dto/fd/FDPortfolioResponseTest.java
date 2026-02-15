package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arpit Chaurasia
 */
@DisplayName("FDPortfolioResponse DTO Tests")
class FDPortfolioResponseTest {

    @Test
    void constructor_setsFields() {
        LocalDate nextMaturity = LocalDate.now().plusDays(20);

        FDPortfolioResponse dto = new FDPortfolioResponse(
                5L,
                3L,
                1L,
                1L,
                new BigDecimal("50000.00"),
                new BigDecimal("1500.00"),
                nextMaturity
        );

        assertThat(dto.getTotalFDs()).isEqualTo(5L);
        assertThat(dto.getActiveFDs()).isEqualTo(3L);
        assertThat(dto.getMaturedFDs()).isEqualTo(1L);
        assertThat(dto.getBrokenFDs()).isEqualTo(1L);
        assertThat(dto.getTotalPrincipal()).isEqualByComparingTo("50000.00");
        assertThat(dto.getTotalInterestAccrued()).isEqualByComparingTo("1500.00");
        assertThat(dto.getNextMaturityDate()).isEqualTo(nextMaturity);
    }
}

