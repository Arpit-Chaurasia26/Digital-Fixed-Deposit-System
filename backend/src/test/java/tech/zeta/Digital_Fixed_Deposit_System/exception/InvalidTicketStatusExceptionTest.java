package tech.zeta.Digital_Fixed_Deposit_System.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Akshaya Siripuram
 */
@DisplayName("InvalidTicketStatusException Tests")
class InvalidTicketStatusExceptionTest {

    @Test
    @DisplayName("Should store message correctly")
    void message_isStored() {
        String message = "Invalid ticket status transition";
        InvalidTicketStatusException ex = new InvalidTicketStatusException(message);
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should throw exception with custom message")
    void throwException_withCustomMessage() {
        String customMessage = "Cannot transition from CLOSED to OPEN status";

        assertThatThrownBy(() -> {
            throw new InvalidTicketStatusException(customMessage);
        })
        .isInstanceOf(InvalidTicketStatusException.class)
        .hasMessage(customMessage);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void isRuntimeException() {
        InvalidTicketStatusException ex = new InvalidTicketStatusException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should handle empty message")
    void handleEmptyMessage() {
        InvalidTicketStatusException ex = new InvalidTicketStatusException("");
        assertThat(ex.getMessage()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null message")
    void handleNullMessage() {
        InvalidTicketStatusException ex = new InvalidTicketStatusException(null);
        assertThat(ex.getMessage()).isNull();
    }

    @Test
    @DisplayName("Should preserve message with special characters")
    void preserveMessageWithSpecialCharacters() {
        String message = "Invalid status: 'PENDING' -> 'INVALID' (unexpected transition)";
        InvalidTicketStatusException ex = new InvalidTicketStatusException(message);
        assertThat(ex.getMessage()).isEqualTo(message);
    }
}

