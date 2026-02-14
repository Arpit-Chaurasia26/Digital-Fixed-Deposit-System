package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// Author - Arpit Chaurasia
@DisplayName("FDMaturityResponse DTO Tests")
class FDMaturityResponseTest {

    @Test
    void constructor_setsFields() {
        LocalDate maturity = LocalDate.now().plusDays(10);

        FDMaturityResponse dto = new FDMaturityResponse(
                1L,
                2L,
                new BigDecimal("5000.00"),
                maturity,
                10L,
                FDStatus.ACTIVE
        );

        assertThat(dto.getFdId()).isEqualTo(1L);
        assertThat(dto.getUserId()).isEqualTo(2L);
        assertThat(dto.getAmount()).isEqualByComparingTo("5000.00");
        assertThat(dto.getMaturityDate()).isEqualTo(maturity);
        assertThat(dto.getDaysRemaining()).isEqualTo(10L);
        assertThat(dto.getStatus()).isEqualTo(FDStatus.ACTIVE);
    }
}

