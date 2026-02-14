package tech.zeta.Digital_Fixed_Deposit_System.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ResourceNotFoundException Tests")
class ResourceNotFoundExceptionTest {

    @Test
    void message_isStored() {
        ResourceNotFoundException ex = new ResourceNotFoundException("missing");
        assertThat(ex.getMessage()).isEqualTo("missing");
    }
}

