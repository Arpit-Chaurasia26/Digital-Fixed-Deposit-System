package tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WithdrawalEligibility DTO Tests")
class WithdrawalEligibilityTest {

    @Test
    void constructor_setsFields() {
        WithdrawalEligibility dto = new WithdrawalEligibility(true, "ok");

        assertThat(dto.isEligible()).isTrue();
        assertThat(dto.getRootCause()).isEqualTo("ok");
    }
}

