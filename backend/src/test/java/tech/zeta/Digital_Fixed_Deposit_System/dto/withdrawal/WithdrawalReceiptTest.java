package tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WithdrawalReceipt DTO Tests")
class WithdrawalReceiptTest {

    @Test
    void constructor_setsFields() {
        LocalDate start = LocalDate.now().minusMonths(6);
        LocalDate maturity = LocalDate.now().plusMonths(6);
        LocalDate close = LocalDate.now();

        WithdrawalReceipt dto = new WithdrawalReceipt(
                1L,
                new BigDecimal("1000.00"),
                new BigDecimal("60.00"),
                new BigDecimal("7.50"),
                start,
                maturity,
                close
        );

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getWithdrawalAmount()).isEqualByComparingTo("1000.00");
        assertThat(dto.getAccruedInterest()).isEqualByComparingTo("60.00");
        assertThat(dto.getInterestRate()).isEqualByComparingTo("7.50");
        assertThat(dto.getStartDate()).isEqualTo(start);
        assertThat(dto.getMaturityDate()).isEqualTo(maturity);
        assertThat(dto.getClosureDate()).isEqualTo(close);
    }
}

