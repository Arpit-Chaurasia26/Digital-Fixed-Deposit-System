package tech.zeta.Digital_Fixed_Deposit_System.entity.support;
public enum TicketStatus {
    OPEN,
    RESOLVED,
    CLOSED;


    // Method to check if a transition is allowed
    public boolean canTransition(TicketStatus newStatus, String role, Long currentUserId, Long ticketUserId) {
        if (this == CLOSED) return false;

        // Admin can move OPEN → RESOLVED
        if (this == OPEN && newStatus == RESOLVED && role.equals("ADMIN")) return true;

        // User can move RESOLVED → CLOSED
        if (this == RESOLVED && newStatus == CLOSED && role.equals("USER") && currentUserId.equals(ticketUserId)) return true;

        // User can also close OPEN tickets directly
        if (this == OPEN && newStatus == CLOSED && role.equals("USER") && currentUserId.equals(ticketUserId)) return true;

        return false;
    }

}
