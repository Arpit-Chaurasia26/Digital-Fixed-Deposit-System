package tech.zeta.Digital_Fixed_Deposit_System.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InvalidOperationException Tests")
class InvalidOperationExceptionTest {

    @Test
    void message_isStored() {
        InvalidOperationException ex = new InvalidOperationException("invalid");
        assertThat(ex.getMessage()).isEqualTo("invalid");
    }
}

