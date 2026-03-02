package tech.zeta.Digital_Fixed_Deposit_System.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Akshaya Siripuram
 */
@DisplayName("TicketNotFoundException Tests")
class TicketNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with correct message format for ticket ID")
    void message_includesTicketId() {
        Long ticketId = 123L;
        TicketNotFoundException ex = new TicketNotFoundException(ticketId);
        assertThat(ex.getMessage()).isEqualTo("Support ticket not found with id: 123");
    }

    @Test
    @DisplayName("Should throw exception when ticket is not found")
    void throwException_whenTicketNotFound() {
        Long ticketId = 456L;

        assertThatThrownBy(() -> {
            throw new TicketNotFoundException(ticketId);
        })
        .isInstanceOf(TicketNotFoundException.class)
        .hasMessage("Support ticket not found with id: 456");
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void isRuntimeException() {
        TicketNotFoundException ex = new TicketNotFoundException(1L);
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should handle zero as ticket ID")
    void handleZeroTicketId() {
        TicketNotFoundException ex = new TicketNotFoundException(0L);
        assertThat(ex.getMessage()).isEqualTo("Support ticket not found with id: 0");
    }

    @Test
    @DisplayName("Should handle negative ticket ID")
    void handleNegativeTicketId() {
        TicketNotFoundException ex = new TicketNotFoundException(-1L);
        assertThat(ex.getMessage()).isEqualTo("Support ticket not found with id: -1");
    }

    @Test
    @DisplayName("Should handle large ticket ID values")
    void handleLargeTicketId() {
        Long largeTicketId = Long.MAX_VALUE;
        TicketNotFoundException ex = new TicketNotFoundException(largeTicketId);
        assertThat(ex.getMessage()).isEqualTo("Support ticket not found with id: " + Long.MAX_VALUE);
    }

    @Test
    @DisplayName("Should preserve ticket ID in message format")
    void preserveTicketIdInMessage() {
        Long ticketId = 999L;
        TicketNotFoundException ex = new TicketNotFoundException(ticketId);

        assertThat(ex.getMessage())
            .contains("Support ticket not found with id:")
            .contains("999");
    }

    @Test
    @DisplayName("Should create new exception instances independently")
    void createMultipleExceptions_independently() {
        TicketNotFoundException ex1 = new TicketNotFoundException(1L);
        TicketNotFoundException ex2 = new TicketNotFoundException(2L);

        assertThat(ex1.getMessage()).isEqualTo("Support ticket not found with id: 1");
        assertThat(ex2.getMessage()).isEqualTo("Support ticket not found with id: 2");
        assertThat(ex1.getMessage()).isNotEqualTo(ex2.getMessage());
    }

    @Test
    @DisplayName("Should be catchable as RuntimeException")
    void catchAsRuntimeException() {
        Long ticketId = 789L;

        assertThatThrownBy(() -> {
            throw new TicketNotFoundException(ticketId);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Support ticket not found with id: 789");
    }

    @Test
    @DisplayName("Should maintain message consistency across multiple calls")
    void messageConsistency() {
        Long ticketId = 555L;
        TicketNotFoundException ex = new TicketNotFoundException(ticketId);

        String message1 = ex.getMessage();
        String message2 = ex.getMessage();

        assertThat(message1).isEqualTo(message2);
    }
}

