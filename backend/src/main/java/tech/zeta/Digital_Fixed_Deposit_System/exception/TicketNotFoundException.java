package tech.zeta.Digital_Fixed_Deposit_System.exception;

/**
 * @author Akshaya Siripuram
 */
public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(Long ticketId) {
        super("Support ticket not found with id: " + ticketId);
    }
}
