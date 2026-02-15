package tech.zeta.Digital_Fixed_Deposit_System.service.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketRequestDTO;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketResponseDTO;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.SupportTicket;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;
import tech.zeta.Digital_Fixed_Deposit_System.service.support.specifications.SupportTicketSpecifications;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;
import tech.zeta.Digital_Fixed_Deposit_System.mapper.SupportTicketMapper;
import tech.zeta.Digital_Fixed_Deposit_System.repository.SupportTicketRepository;
import tech.zeta.Digital_Fixed_Deposit_System.repository.UserRepository;
import org.springframework.data.jpa.domain.Specification;
import tech.zeta.Digital_Fixed_Deposit_System.exception.TicketNotFoundException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedTicketActionException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;



/**
 * @author Akshaya Siripuram
 */
@Slf4j
@Service
@Transactional
public class SupportTicketServiceImpl implements SupportTicketService {
    
    private final SupportTicketRepository repository;
    private final SupportTicketMapper mapper;
    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;
    private final FixedDepositRepository fdRepository;

    public SupportTicketServiceImpl(SupportTicketRepository repository,
                                    SupportTicketMapper mapper,
                                    CurrentUserProvider currentUserProvider,
                                    UserRepository userRepository,
                                    FixedDepositRepository fdRepository ) {
        this.repository = repository;
        this.mapper = mapper;
        this.currentUserProvider = currentUserProvider;
        this.userRepository = userRepository;
        this.fdRepository = fdRepository;
    }

    // -------------------------------
    // CREATE TICKET (USER ONLY)
    // -------------------------------
    @Override
    public SupportTicketResponseDTO createTicket(SupportTicketRequestDTO requestDTO) {
        User currentUser = getCurrentUser();

        // Admins are not allowed to create tickets
        if (isAdmin(currentUser)) {
            log.warn("Admin {} attempted to create a ticket, which is not allowed", currentUser.getId());
            throw new UnauthorizedTicketActionException("Admins cannot create tickets");
        }

        requestDTO.setUserId(currentUser.getId());

        // Validate FD ownership
        if (!checkUserOwnsFD(currentUser.getId(), requestDTO.getFdId())) {
            log.warn("User {} tried to create ticket for FD {} which they do not own", currentUser.getId(), requestDTO.getFdId());
            throw new UnauthorizedTicketActionException("Invalid FD ID: You do not own this FD");
        }

        // Create ticket entity and set default properties
        SupportTicket ticket = mapper.toEntity(requestDTO);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedTime(LocalDateTime.now());
        ticket.setUpdatedTime(LocalDateTime.now());

        SupportTicket saved = repository.save(ticket);

        // Log ticket creation
        log.info("Ticket {} created by User {}", saved.getId(), currentUser.getId());

        return mapper.toResponseDTO(saved);
    }

    // -------------------------------
    // GET TICKET BY ID (ADMIN ONLY)
    // -------------------------------
    @Override
    public SupportTicketResponseDTO getTicketById(Long ticketId) {
        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser)) {
            log.warn("User {} attempted to access ticket {} but is not an admin", currentUser.getId(), ticketId);
            throw new UnauthorizedTicketActionException("Only admins can access tickets by ID");
        }

        SupportTicket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        log.info("Admin {} retrieved ticket {}", currentUser.getId(), ticketId);

        return mapper.toResponseDTO(ticket);
    }

    // -------------------------------
    // GET TICKETS OF LOGGED-IN USER
    // -------------------------------
    @Override
    public List<SupportTicketResponseDTO> getTicketsByUserId() {
        User currentUser = getCurrentUser();

        if (!isUser(currentUser)) {
            log.warn("Admin {} attempted to access personal tickets (not allowed)", currentUser.getId());
            throw new UnauthorizedTicketActionException("Admins do not have personal tickets");
        }

        List<SupportTicketResponseDTO> tickets = repository.findByUserId(currentUser.getId())
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());

        log.info("User {} retrieved {} tickets", currentUser.getId(), tickets.size());

        return tickets;
    }

    // -------------------------------
    // GET ALL TICKETS (ADMIN ONLY)
    // -------------------------------
    @Override
    public Page<SupportTicketResponseDTO> getAllTickets(
            TicketStatus status,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Long userId,
            Long fdId,
            Pageable pageable
    ) {
        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser)) {
            log.warn("User {} attempted to retrieve all tickets but is not admin", currentUser.getId());
            throw new UnauthorizedTicketActionException("Only admins can access all tickets");
        }

        // Build specification with all filters
        Specification<SupportTicket> spec = Specification
                .where(SupportTicketSpecifications.hasStatus(status))
                .and(SupportTicketSpecifications.createdAfter(fromDate))
                .and(SupportTicketSpecifications.createdBefore(toDate))
                .and(SupportTicketSpecifications.updatedAfter(fromDate))
                .and(SupportTicketSpecifications.updatedBefore(toDate))
                .and(SupportTicketSpecifications.hasUserId(userId))
                .and(SupportTicketSpecifications.hasFdId(fdId));

        Page<SupportTicketResponseDTO> result = repository.findAll(spec, pageable)
                .map(mapper::toResponseDTO);

        log.info("Admin {} retrieved {} tickets with filters", currentUser.getId(), result.getTotalElements());

        return result;
    }

    // -------------------------------
    // UPDATE TICKET STATUS
    // -------------------------------
    @Override
    public SupportTicketResponseDTO updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        // 1. Find the ticket
        SupportTicket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        // 2. Get the current logged-in user
        User currentUser = getCurrentUser();

        // 3. Check if the transition is allowed using enum logic
        boolean allowed = ticket.getStatus()
                .canTransition(newStatus, currentUser.getRole().name(), currentUser.getId(), ticket.getUserId());

        if (!allowed) {
            log.warn("User {} (role: {}) tried invalid status transition from {} to {} for ticket {}",
                    currentUser.getId(), currentUser.getRole().name(), ticket.getStatus(), newStatus, ticket.getId());
            throw new UnauthorizedTicketActionException(
                    "You cannot change ticket status from " + ticket.getStatus() + " to " + newStatus +
                            " as a " + currentUser.getRole().name()
            );
        }

        // 4. Update status and updated time
        ticket.setStatus(newStatus);
        ticket.setUpdatedTime(LocalDateTime.now());

        // 5. Save ticket
        SupportTicket updated = repository.save(ticket);

        // 6. Log status update
        log.info("Ticket {} status changed to {} by User {}", ticket.getId(), newStatus, currentUser.getId());

        return mapper.toResponseDTO(updated);
    }

    // -------------------------------
    // UPDATE TICKET RESPONSE (ADMIN ONLY)
    // -------------------------------
    @Override
    public SupportTicketResponseDTO updateTicketResponse(Long ticketId, String response) {
        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser)) {
            log.warn("User {} attempted to update ticket response but is not admin", currentUser.getId());
            throw new UnauthorizedTicketActionException("Only admins can update ticket responses");
        }

        SupportTicket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        ticket.setResponse(response);

        // Automatically mark OPEN tickets as RESOLVED when admin responds
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.RESOLVED);
        }

        ticket.setUpdatedTime(LocalDateTime.now());

        SupportTicket updated = repository.save(ticket);

        log.info("Admin {} updated response for Ticket {}. Status now {}", currentUser.getId(), ticket.getId(), ticket.getStatus());

        return mapper.toResponseDTO(updated);
    }

    // -------------------------------
    // HELPER METHODS
    // -------------------------------

    // Get currently logged-in user
    private User getCurrentUser() {
        Long userId = currentUserProvider.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // Check if user is admin
    private boolean isAdmin(User user) {
        return user.getRole() != null && user.getRole().name().equals("ADMIN");
    }

    // Check if user is regular USER
    private boolean isUser(User user) {
        return user.getRole() != null && user.getRole().name().equals("USER");
    }

    // Check if user owns the FD (if provided)
    private boolean checkUserOwnsFD(Long userId, Long fdId) {
        if (fdId == null) return true; // optional FD
        return fdRepository.findByIdAndUserId(fdId, userId).isPresent();
    }

}
