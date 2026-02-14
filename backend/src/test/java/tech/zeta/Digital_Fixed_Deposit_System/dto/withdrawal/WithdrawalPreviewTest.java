package tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WithdrawalPreview DTO Tests")
class WithdrawalPreviewTest {

    @Test
    void constructor_setsFields() {
        WithdrawalPreview dto = new WithdrawalPreview(
                new BigDecimal("1000.00"),
                new BigDecimal("40.00"),
                new BigDecimal("7.50"),
                new BigDecimal("10.00"),
                new BigDecimal("30.00"),
                new BigDecimal("990.00")
        );

        assertThat(dto.getWithdrawalAmount()).isEqualByComparingTo("1000.00");
        assertThat(dto.getAccumulatedInterestAmount()).isEqualByComparingTo("40.00");
        assertThat(dto.getInterestRate()).isEqualByComparingTo("7.50");
        assertThat(dto.getPenalty()).isEqualByComparingTo("10.00");
        assertThat(dto.getNetInterestAmount()).isEqualByComparingTo("30.00");
        assertThat(dto.getBalanceAmount()).isEqualByComparingTo("990.00");
    }
}

