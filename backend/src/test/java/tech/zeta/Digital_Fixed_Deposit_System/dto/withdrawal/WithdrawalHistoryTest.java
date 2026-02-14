package tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WithdrawalHistory DTO Tests")
class WithdrawalHistoryTest {

    @Test
    void constructor_setsFields() {
        LocalDate date = LocalDate.now();
        WithdrawalHistory dto = new WithdrawalHistory(1L, new BigDecimal("1000.00"), new BigDecimal("50.00"), date);

        assertThat(dto.getFdId()).isEqualTo(1L);
        assertThat(dto.getWithdrawalAmount()).isEqualByComparingTo("1000.00");
        assertThat(dto.getInterestPaid()).isEqualByComparingTo("50.00");
        assertThat(dto.getWithdrawnDate()).isEqualTo(date);
    }
}

