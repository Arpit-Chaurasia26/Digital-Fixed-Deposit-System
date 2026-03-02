package tech.zeta.Digital_Fixed_Deposit_System.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Akshaya Siripuram
 */
@DisplayName("UnauthorizedTicketActionException Tests")
class UnauthorizedTicketActionExceptionTest {

    @Test
    @DisplayName("Should store message correctly")
    void message_isStored() {
        String message = "User is not authorized to perform this action on the ticket";
        UnauthorizedTicketActionException ex = new UnauthorizedTicketActionException(message);
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should throw exception with custom message")
    void throwException_withCustomMessage() {
        String customMessage = "Only ticket creator can close a ticket";

        assertThatThrownBy(() -> {
            throw new UnauthorizedTicketActionException(customMessage);
        })
        .isInstanceOf(UnauthorizedTicketActionException.class)
        .hasMessage(customMessage);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void isRuntimeException() {
        UnauthorizedTicketActionException ex = new UnauthorizedTicketActionException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should handle empty message")
    void handleEmptyMessage() {
        UnauthorizedTicketActionException ex = new UnauthorizedTicketActionException("");
        assertThat(ex.getMessage()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null message")
    void handleNullMessage() {
        UnauthorizedTicketActionException ex = new UnauthorizedTicketActionException(null);
        assertThat(ex.getMessage()).isNull();
    }

    @Test
    @DisplayName("Should preserve message with special characters")
    void preserveMessageWithSpecialCharacters() {
        String message = "Unauthorized action: User#123 cannot perform 'CLOSE' on ticket#456";
        UnauthorizedTicketActionException ex = new UnauthorizedTicketActionException(message);
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should throw exception for unauthorized admin operations")
    void throwException_forUnauthorizedAdminAction() {
        String message = "Admin privileges required to escalate ticket";

        assertThatThrownBy(() -> {
            throw new UnauthorizedTicketActionException(message);
        })
        .isInstanceOf(UnauthorizedTicketActionException.class)
        .hasMessage(message);
    }

    @Test
    @DisplayName("Should throw exception for non-ticket-owner actions")
    void throwException_forNonOwnerAction() {
        String message = "Only the ticket owner can update the ticket";

        assertThatThrownBy(() -> {
            throw new UnauthorizedTicketActionException(message);
        })
        .isInstanceOf(UnauthorizedTicketActionException.class)
        .hasMessage(message);
    }

    @Test
    @DisplayName("Should preserve message with long descriptions")
    void preserveMessageWithLongDescription() {
        String message = "Unauthorized ticket action: User does not have permission to perform " +
                "the requested operation on this support ticket. " +
                "Only the ticket creator, assigned support staff, or administrators can modify tickets.";
        UnauthorizedTicketActionException ex = new UnauthorizedTicketActionException(message);
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should be catchable as RuntimeException")
    void catchAsRuntimeException() {
        String message = "Insufficient permissions";

        assertThatThrownBy(() -> {
            throw new UnauthorizedTicketActionException(message);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessage(message);
    }

    @Test
    @DisplayName("Should maintain message consistency across multiple calls")
    void messageConsistency() {
        String message = "User cannot modify assigned tickets";
        UnauthorizedTicketActionException ex = new UnauthorizedTicketActionException(message);

        String message1 = ex.getMessage();
        String message2 = ex.getMessage();

        assertThat(message1).isEqualTo(message2);
    }

    @Test
    @DisplayName("Should create multiple independent exception instances")
    void createMultipleExceptions_independently() {
        String message1 = "Cannot delete ticket";
        String message2 = "Cannot reassign ticket";

        UnauthorizedTicketActionException ex1 = new UnauthorizedTicketActionException(message1);
        UnauthorizedTicketActionException ex2 = new UnauthorizedTicketActionException(message2);

        assertThat(ex1.getMessage()).isEqualTo(message1);
        assertThat(ex2.getMessage()).isEqualTo(message2);
        assertThat(ex1.getMessage()).isNotEqualTo(ex2.getMessage());
    }

    @Test
    @DisplayName("Should be throwable in permission check scenarios")
    void throwInPermissionCheckScenario() {
        String userId = "user123";
        String ticketId = "ticket456";
        String message = String.format("User %s is not authorized to access ticket %s", userId, ticketId);

        assertThatThrownBy(() -> {
            validateUserPermission(userId, ticketId);
        })
        .isInstanceOf(UnauthorizedTicketActionException.class)
        .hasMessage(message);
    }

    /**
     * Helper method to simulate permission validation
     */
    private void validateUserPermission(String userId, String ticketId) {
        String message = String.format("User %s is not authorized to access ticket %s", userId, ticketId);
        throw new UnauthorizedTicketActionException(message);
    }

    @Test
    @DisplayName("Should preserve authorization context in message")
    void preserveAuthorizationContext() {
        String action = "CLOSE";
        String role = "user";
        String message = String.format("Action '%s' requires role 'admin' but user has role '%s'", action, role);

        UnauthorizedTicketActionException ex = new UnauthorizedTicketActionException(message);
        assertThat(ex.getMessage())
            .contains(action)
            .contains("admin")
            .contains(role);
    }
}

