package tech.zeta.Digital_Fixed_Deposit_System.service.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketRequestDTO;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketResponseDTO;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;

import java.time.LocalDateTime;
import java.util.List;

/*
    Author - Akshaya Siripuram
 */

public interface SupportTicketService {

    // Create a new support ticket for logged-in user.
    SupportTicketResponseDTO createTicket(SupportTicketRequestDTO requestDTO);

    // Get a ticket by ID. Users can access only their tickets, admin can access all.
    SupportTicketResponseDTO getTicketById(Long ticketId);

    // Get all tickets of the logged-in user.
    List<SupportTicketResponseDTO> getTicketsByUserId();

    // Get all tickets in the system (admin only).
    public Page<SupportTicketResponseDTO> getAllTickets(
            TicketStatus status,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            Long userId,
            Long fdId,
            Pageable pageable
    );

    // Update the status of a ticket (RESOLVED by admin, CLOSED by user).
    SupportTicketResponseDTO updateTicketStatus(Long ticketId, TicketStatus status);

    // Update the response of a ticket (admin only).
    SupportTicketResponseDTO updateTicketResponse(Long ticketId, String response);
}
