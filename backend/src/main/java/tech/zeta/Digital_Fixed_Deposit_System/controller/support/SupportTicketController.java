package tech.zeta.Digital_Fixed_Deposit_System.controller.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketRequestDTO;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketResponseDTO;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;
import tech.zeta.Digital_Fixed_Deposit_System.service.support.SupportTicketService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/support")
public class SupportTicketController {

    private static final Logger logger = LoggerFactory.getLogger(SupportTicketController.class);

    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    // Create a new ticket (User only, Admin cannot create)
    @PostMapping
    public SupportTicketResponseDTO createTicket(@RequestBody SupportTicketRequestDTO requestDTO) {
        logger.info("Creating support ticket");
        return supportTicketService.createTicket(requestDTO);
    }

    // Get logged-in user's tickets (User only)
    @GetMapping("/my-tickets")
    public List<SupportTicketResponseDTO> getMyTickets() {
        logger.info("Fetching current user's support tickets");
        return supportTicketService.getTicketsByUserId();
    }

    // Get ticket by ID (Admin only)
    @GetMapping("/{ticketId}")
    public SupportTicketResponseDTO getTicketById(@PathVariable Long ticketId) {
        logger.info("Fetching support ticket by id: ticketId={}", ticketId);
        return supportTicketService.getTicketById(ticketId);
    }

    // Update ticket status (Admin can mark RESOLVED, User can mark CLOSED)
    @PatchMapping("/{ticketId}/status")
    public SupportTicketResponseDTO updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestBody String statusString
    ) {
        logger.info("Updating support ticket status: ticketId={}, status={}", ticketId, statusString);
        TicketStatus status = TicketStatus.valueOf(statusString);
        return supportTicketService.updateTicketStatus(ticketId, status);
    }

    // Get all tickets (Admin only) - WITH userId and fdId FILTERS
    @GetMapping
    public Page<SupportTicketResponseDTO> getAllTickets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long fdId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("Fetching support tickets: status={}, createdFrom={}, createdTo={}, userId={}, fdId={}, page={}, size={}",
                status, createdFrom, createdTo, userId, fdId, page, size);
        TicketStatus ticketStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                ticketStatus = TicketStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid support ticket status filter: status={}", status);
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
        return supportTicketService.getAllTickets(ticketStatus, createdFrom, createdTo, userId, fdId, pageable);
    }


    // Update ticket response (Admin only)
    @PatchMapping(value = "/{ticketId}/response", consumes = { MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public SupportTicketResponseDTO updateTicketResponse(
            @PathVariable Long ticketId,
            @RequestBody String response
    ) {
        logger.info("Updating support ticket response: ticketId={}", ticketId);
        return supportTicketService.updateTicketResponse(ticketId, response);
    }
}
