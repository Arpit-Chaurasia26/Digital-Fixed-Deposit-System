package tech.zeta.Digital_Fixed_Deposit_System.service.support;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
                                    FixedDepositRepository fdRepository ) { //injecting here
        this.repository = repository;
        this.mapper = mapper;
        this.currentUserProvider = currentUserProvider;
        this.userRepository = userRepository;
        this.fdRepository = fdRepository; // assign
    }

    // 🔹 Create a new ticket (User only)
    @Override
    public SupportTicketResponseDTO createTicket(SupportTicketRequestDTO requestDTO) {
        User currentUser = getCurrentUser();

        if (isAdmin(currentUser)) {
            throw new RuntimeException("Admins cannot create tickets");
        }

        requestDTO.setUserId(currentUser.getId());

        // ✅ Validate FD ownership
        if (!checkUserOwnsFD(currentUser.getId(), requestDTO.getFdId())) {
            throw new RuntimeException("Invalid FD ID: You do not own this FD");
        }

        SupportTicket ticket = mapper.toEntity(requestDTO);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedTime(LocalDateTime.now());
        ticket.setUpdatedTime(LocalDateTime.now());

        SupportTicket saved = repository.save(ticket);
        return mapper.toResponseDTO(saved);
    }

    // 🔹 Get ticket by ID (Admin only)
    @Override
    public SupportTicketResponseDTO getTicketById(Long ticketId) {
        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new RuntimeException("Only admins can access tickets by ID");
        }

        SupportTicket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        return mapper.toResponseDTO(ticket);
    }

    // 🔹 Get tickets of logged-in user (User only)
    @Override
    public List<SupportTicketResponseDTO> getTicketsByUserId() {
        User currentUser = getCurrentUser();

        if (!isUser(currentUser)) {
            throw new RuntimeException("Admins do not have personal tickets");
        }

        return repository.findByUserId(currentUser.getId())
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // 🔹 Get all tickets (Admin only)
    // Admin only

    @Override
    public Page<SupportTicketResponseDTO> getAllTickets(TicketStatus status, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        User currentUser = getCurrentUser();
        if (!isAdmin(currentUser)) {
            throw new RuntimeException("Only admins can access all tickets");
        }

        Specification<SupportTicket> spec = Specification
                .where(SupportTicketSpecifications.hasStatus(status))
                .and(SupportTicketSpecifications.createdAfter(fromDate))
                .and(SupportTicketSpecifications.createdBefore(toDate))
                .and(SupportTicketSpecifications.updatedAfter(fromDate))
                .and(SupportTicketSpecifications.updatedBefore(toDate));

        return repository.findAll(spec, pageable)
                .map(mapper::toResponseDTO);
    }

    // 🔹 Update ticket status
    @Override
    public SupportTicketResponseDTO updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        SupportTicket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User currentUser = getCurrentUser();
        TicketStatus currentStatus = ticket.getStatus();

        // ❌ Prevent backward movement
        if (currentStatus == TicketStatus.CLOSED) {
            throw new RuntimeException("Closed tickets cannot be updated");
        }

        if (currentStatus == TicketStatus.RESOLVED && newStatus == TicketStatus.OPEN) {
            throw new RuntimeException("Cannot move RESOLVED ticket back to OPEN");
        }

        // Admin rules
        if (isAdmin(currentUser)) {
            if (newStatus != TicketStatus.RESOLVED) {
                throw new RuntimeException("Admins can only mark tickets as RESOLVED");
            }
        }

        // User rules
        if (isUser(currentUser)) {
            if (newStatus != TicketStatus.CLOSED) {
                throw new RuntimeException("Users can only mark their tickets as CLOSED");
            }
            if (!ticket.getUserId().equals(currentUser.getId())) {
                throw new RuntimeException("Users can only close their own tickets");
            }
        }

        ticket.setStatus(newStatus);
        ticket.setUpdatedTime(LocalDateTime.now());
        SupportTicket updated = repository.save(ticket);

        return mapper.toResponseDTO(updated);
    }

    // 🔹 Update ticket response (Admin only)
    @Override
    public SupportTicketResponseDTO updateTicketResponse(Long ticketId, String response) {
        User currentUser = getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new RuntimeException("Only admins can update ticket responses");
        }

        SupportTicket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setResponse(response);
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.RESOLVED);
        }
        ticket.setUpdatedTime(LocalDateTime.now());

        SupportTicket updated = repository.save(ticket);
        return mapper.toResponseDTO(updated);
    }

    // 🔹 Helper to fetch logged-in User entity
    private User getCurrentUser() {
        Long userId = currentUserProvider.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 🔹 Helper: check role
    private boolean isAdmin(User user) {
        return user.getRole() != null && user.getRole().name().equals("ADMIN");
    }

    private boolean isUser(User user) {
        return user.getRole() != null && user.getRole().name().equals("USER");
    }

    // Helper: to check the user fd's
    private boolean checkUserOwnsFD(Long userId, Long fdId) {
        if (fdId == null) return true; // optional FD
        return fdRepository.findByIdAndUserId(fdId, userId).isPresent();
    }

}
