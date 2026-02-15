package tech.zeta.Digital_Fixed_Deposit_System.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InSufficientFundsException Tests")
class InSufficientFundsExceptionTest {

    @Test
    void message_isStored() {
        InSufficientFundsException ex = new InSufficientFundsException("funds");
        assertThat(ex.getMessage()).isEqualTo("funds");
    }
}

