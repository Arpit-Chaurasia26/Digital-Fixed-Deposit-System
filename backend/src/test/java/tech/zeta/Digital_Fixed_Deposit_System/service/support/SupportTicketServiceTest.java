package tech.zeta.Digital_Fixed_Deposit_System.service.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketRequestDTO;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketResponseDTO;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.SupportTicket;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;
import tech.zeta.Digital_Fixed_Deposit_System.mapper.SupportTicketMapper;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;
import tech.zeta.Digital_Fixed_Deposit_System.repository.SupportTicketRepository;
import tech.zeta.Digital_Fixed_Deposit_System.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SupportTicketServiceImpl Unit Tests - Full Coverage")
class SupportTicketServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(SupportTicketServiceTest.class);

    @Mock private SupportTicketRepository repository;
    @Mock private SupportTicketMapper mapper;
    @Mock private CurrentUserProvider currentUserProvider;
    @Mock private UserRepository userRepository;
    @Mock private FixedDepositRepository fdRepository;

    @InjectMocks
    private SupportTicketServiceImpl supportTicketService;

    private static final Long USER_ID = 1L;
    private static final Long ADMIN_ID = 2L;
    private static final Long TICKET_ID = 100L;
    private static final Long FD_ID = 50L;

    private User testUser;
    private User adminUser;
    private SupportTicket testTicket;
    private SupportTicketResponseDTO testResponseDTO;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "user@test.com", "password", Role.USER);
        setField(testUser, "id", USER_ID);

        adminUser = new User("Admin User", "admin@test.com", "password", Role.ADMIN);
        setField(adminUser, "id", ADMIN_ID);

        testTicket = new SupportTicket();
        setField(testTicket, "id", TICKET_ID);
        testTicket.setUserId(USER_ID);
        testTicket.setSubject("Test Subject");
        testTicket.setDescription("Test Description");
        testTicket.setStatus(TicketStatus.OPEN);
        testTicket.setFdId(FD_ID);

        testResponseDTO = new SupportTicketResponseDTO();
        testResponseDTO.setId(TICKET_ID);
        testResponseDTO.setUserId(USER_ID);
        testResponseDTO.setSubject("Test Subject");
        testResponseDTO.setStatus(TicketStatus.OPEN);
    }

    @Nested
    @DisplayName("Create Ticket Tests")
    class CreateTicketTests {

        @Test
        @DisplayName("Should create ticket successfully for user")
        void createTicket_ValidUser_ShouldSucceed() {
            SupportTicketRequestDTO request = new SupportTicketRequestDTO();
            request.setSubject("Issue");
            request.setDescription("Description");
            request.setFdId(FD_ID);

            when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(fdRepository.findByIdAndUserId(FD_ID, USER_ID)).thenReturn(Optional.of(new tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit()));
            when(mapper.toEntity(any())).thenReturn(testTicket);
            when(repository.save(any())).thenReturn(testTicket);
            when(mapper.toResponseDTO(any())).thenReturn(testResponseDTO);

            SupportTicketResponseDTO response = supportTicketService.createTicket(request);

            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isEqualTo(TicketStatus.OPEN);
            verify(repository).save(any(SupportTicket.class));
            logger.info("Create ticket test passed");
        }

        @Test
        @DisplayName("Should reject ticket creation by admin")
        void createTicket_AdminUser_ShouldThrow() {
            SupportTicketRequestDTO request = new SupportTicketRequestDTO();
            when(currentUserProvider.getCurrentUserId()).thenReturn(ADMIN_ID);
            when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));

            assertThatThrownBy(() -> supportTicketService.createTicket(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Admins cannot create");
            logger.info("Admin create ticket rejection test passed");
        }

        @Test
        @DisplayName("Should reject ticket with invalid FD")
        void createTicket_InvalidFD_ShouldThrow() {
            SupportTicketRequestDTO request = new SupportTicketRequestDTO();
            request.setFdId(999L);
            when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(fdRepository.findByIdAndUserId(999L, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> supportTicketService.createTicket(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid FD");
            logger.info("Invalid FD test passed");
        }
    }

    @Nested
    @DisplayName("Get Ticket By ID Tests")
    class GetTicketByIdTests {

        @Test
        @DisplayName("Should get ticket by ID for admin")
        void getTicketById_AdminUser_ShouldSucceed() {
            when(currentUserProvider.getCurrentUserId()).thenReturn(ADMIN_ID);
            when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
            when(repository.findById(TICKET_ID)).thenReturn(Optional.of(testTicket));
            when(mapper.toResponseDTO(testTicket)).thenReturn(testResponseDTO);

            SupportTicketResponseDTO response = supportTicketService.getTicketById(TICKET_ID);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(TICKET_ID);
            logger.info("Get ticket by ID admin test passed");
        }

        @Test
        @DisplayName("Should reject non-admin access")
        void getTicketById_NonAdmin_ShouldThrow() {
            when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            assertThatThrownBy(() -> supportTicketService.getTicketById(TICKET_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Only admins");
            logger.info("Non-admin access rejection test passed");
        }

        @Test
        @DisplayName("Should throw when ticket not found")
        void getTicketById_NotFound_ShouldThrow() {
            when(currentUserProvider.getCurrentUserId()).thenReturn(ADMIN_ID);
            when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
            when(repository.findById(TICKET_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> supportTicketService.getTicketById(TICKET_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("not found");
            logger.info("Ticket not found test passed");
        }
    }

    @Nested
    @DisplayName("Get Tickets By User ID Tests")
    class GetTicketsByUserIdTests {

        @Test
        @DisplayName("Should get user's tickets")
        void getTicketsByUserId_ValidUser_ShouldSucceed() {
            when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(repository.findByUserId(USER_ID)).thenReturn(Arrays.asList(testTicket));
            when(mapper.toResponseDTO(any())).thenReturn(testResponseDTO);

            List<SupportTicketResponseDTO> response = supportTicketService.getTicketsByUserId();

            assertThat(response).hasSize(1);
            logger.info("Get user tickets test passed");
        }

        @Test
        @DisplayName("Should reject admin access to personal tickets")
        void getTicketsByUserId_Admin_ShouldThrow() {
            when(currentUserProvider.getCurrentUserId()).thenReturn(ADMIN_ID);
            when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));

            assertThatThrownBy(() -> supportTicketService.getTicketsByUserId())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Admins do not have");
            logger.info("Admin personal tickets rejection test passed");
        }
    }

    @Nested
    @DisplayName("Update Ticket Status Tests")
    class UpdateTicketStatusTests {

        @Test
        @DisplayName("Should allow admin to resolve ticket")
        void updateStatus_AdminResolve_ShouldSucceed() {
            when(currentUserProvider.getCurrentUserId()).thenReturn(ADMIN_ID);
            when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
            when(repository.findById(TICKET_ID)).thenReturn(Optional.of(testTicket));
            when(repository.save(any())).thenReturn(testTicket);
            when(mapper.toResponseDTO(any())).thenReturn(testResponseDTO);

            SupportTicketResponseDTO response = supportTicketService.updateTicketStatus(TICKET_ID, TicketStatus.RESOLVED);

            assertThat(response).isNotNull();
            verify(repository).save(any(SupportTicket.class));
            logger.info("Admin resolve ticket test passed");
        }

        @Test
        @DisplayName("Should allow user to close their ticket")
        void updateStatus_UserClose_ShouldSucceed() {
            testTicket.setStatus(TicketStatus.RESOLVED);
            when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(repository.findById(TICKET_ID)).thenReturn(Optional.of(testTicket));
            when(repository.save(any())).thenReturn(testTicket);
            when(mapper.toResponseDTO(any())).thenReturn(testResponseDTO);

            SupportTicketResponseDTO response = supportTicketService.updateTicketStatus(TICKET_ID, TicketStatus.CLOSED);

            assertThat(response).isNotNull();
            logger.info("User close ticket test passed");
        }
    }

    @Nested
    @DisplayName("Update Ticket Response Tests")
    class UpdateTicketResponseTests {

        @Test
        @DisplayName("Should allow admin to update response")
        void updateResponse_Admin_ShouldSucceed() {
            when(currentUserProvider.getCurrentUserId()).thenReturn(ADMIN_ID);
            when(userRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminUser));
            when(repository.findById(TICKET_ID)).thenReturn(Optional.of(testTicket));
            when(repository.save(any())).thenReturn(testTicket);
            when(mapper.toResponseDTO(any())).thenReturn(testResponseDTO);

            SupportTicketResponseDTO response = supportTicketService.updateTicketResponse(TICKET_ID, "Admin response");

            assertThat(response).isNotNull();
            verify(repository).save(any(SupportTicket.class));
            logger.info("Admin update response test passed");
        }

        @Test
        @DisplayName("Should reject non-admin response update")
        void updateResponse_NonAdmin_ShouldThrow() {
            when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            assertThatThrownBy(() -> supportTicketService.updateTicketResponse(TICKET_ID, "Response"))
                    .isInstanceOf(RuntimeException.class);
            logger.info("Non-admin response rejection test passed");
        }
    }

    // Helper methods
    private void setField(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) { }
    }
}

