package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

// Author - Arpit Chaurasia
@DisplayName("FDFinancialYearSummaryResponse DTO Tests")
class FDFinancialYearSummaryResponseTest {

    @Test
    void constructor_setsFields() {
        FDFinancialYearSummaryResponse dto = new FDFinancialYearSummaryResponse(
                "2025-26",
                3L,
                new BigDecimal("15000.00"),
                new BigDecimal("600.00")
        );

        assertThat(dto.getFinancialYear()).isEqualTo("2025-26");
        assertThat(dto.getTotalFDsCreated()).isEqualTo(3L);
        assertThat(dto.getTotalPrincipalDeposited()).isEqualByComparingTo("15000.00");
        assertThat(dto.getTotalInterestAccruedTillDate()).isEqualByComparingTo("600.00");
    }
}

