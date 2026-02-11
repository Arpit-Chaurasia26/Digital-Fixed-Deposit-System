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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/support")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    // 🔹 Create a new ticket (User only, Admin cannot create)
    @PostMapping
    public SupportTicketResponseDTO createTicket(@RequestBody SupportTicketRequestDTO requestDTO) {
        return supportTicketService.createTicket(requestDTO);
    }

    // 🔹 Get logged-in user's tickets (User only)
    @GetMapping("/my-tickets")
    public List<SupportTicketResponseDTO> getMyTickets() {
        return supportTicketService.getTicketsByUserId();
    }

    // 🔹 Get ticket by ID (Admin only)
    @GetMapping("/{ticketId}")
    public SupportTicketResponseDTO getTicketById(@PathVariable Long ticketId) {
        return supportTicketService.getTicketById(ticketId);
    }

    // 🔹 Update ticket status (Admin can mark RESOLVED, User can mark CLOSED)
    @PatchMapping("/{ticketId}/status")
    public SupportTicketResponseDTO updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestBody TicketStatus status
    ) {
        return supportTicketService.updateTicketStatus(ticketId, status);
    }

    // 🔹 Get all tickets (Admin only)
    @GetMapping
    public Page<SupportTicketResponseDTO> getAllTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());
        return supportTicketService.getAllTickets(status, createdFrom, createdTo, pageable);
    }


    // 🔹 Update ticket response (Admin only)
    @PatchMapping(value = "/{ticketId}/response", consumes = { MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public SupportTicketResponseDTO updateTicketResponse(
            @PathVariable Long ticketId,
            @RequestBody String response
    ) {
        return supportTicketService.updateTicketResponse(ticketId, response);
    }
}
