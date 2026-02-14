package tech.zeta.Digital_Fixed_Deposit_System.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BusinessException Tests")
class BusinessExceptionTest {

    @Test
    void message_isStored() {
        BusinessException ex = new BusinessException("msg");
        assertThat(ex.getMessage()).isEqualTo("msg");
    }
}

