package tech.zeta.Digital_Fixed_Deposit_System.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UnauthorizedException Tests")
class UnauthorizedExceptionTest {

    @Test
    void message_isStored() {
        UnauthorizedException ex = new UnauthorizedException("nope");
        assertThat(ex.getMessage()).isEqualTo("nope");
    }
}

