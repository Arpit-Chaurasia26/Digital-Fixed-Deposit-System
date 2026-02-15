package tech.zeta.Digital_Fixed_Deposit_System.controller.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketRequestDTO;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketResponseDTO;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;
import tech.zeta.Digital_Fixed_Deposit_System.service.support.SupportTicketService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@DisplayName("SupportTicketController Unit Tests")
class SupportTicketControllerTest {

    @Test
    @DisplayName("createTicket delegates to service")
    void createTicket_delegates() {
        SupportTicketService service = Mockito.mock(SupportTicketService.class);
        SupportTicketController controller = new SupportTicketController(service);

        SupportTicketRequestDTO request = new SupportTicketRequestDTO();
        SupportTicketResponseDTO response = new SupportTicketResponseDTO();
        response.setId(1L);

        when(service.createTicket(any())).thenReturn(response);

        SupportTicketResponseDTO result = controller.createTicket(request);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getMyTickets returns list")
    void getMyTickets_returnsList() {
        SupportTicketService service = Mockito.mock(SupportTicketService.class);
        SupportTicketController controller = new SupportTicketController(service);

        when(service.getTicketsByUserId()).thenReturn(List.of(new SupportTicketResponseDTO()));

        assertThat(controller.getMyTickets()).hasSize(1);
    }

    @Test
    @DisplayName("getTicketById delegates to service")
    void getTicketById_delegates() {
        SupportTicketService service = Mockito.mock(SupportTicketService.class);
        SupportTicketController controller = new SupportTicketController(service);

        SupportTicketResponseDTO dto = new SupportTicketResponseDTO();
        dto.setId(5L);
        when(service.getTicketById(5L)).thenReturn(dto);

        assertThat(controller.getTicketById(5L).getId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("updateTicketStatus parses enum")
    void updateTicketStatus_parsesEnum() {
        SupportTicketService service = Mockito.mock(SupportTicketService.class);
        SupportTicketController controller = new SupportTicketController(service);

        SupportTicketResponseDTO dto = new SupportTicketResponseDTO();
        dto.setStatus(TicketStatus.RESOLVED);
        when(service.updateTicketStatus(eq(1L), eq(TicketStatus.RESOLVED))).thenReturn(dto);

        assertThat(controller.updateTicketStatus(1L, "RESOLVED").getStatus())
                .isEqualTo(TicketStatus.RESOLVED);
    }

    @Test
    @DisplayName("getAllTickets delegates with paging")
    void getAllTickets_delegates() {
        SupportTicketService service = Mockito.mock(SupportTicketService.class);
        SupportTicketController controller = new SupportTicketController(service);

        Page<SupportTicketResponseDTO> page = new PageImpl<>(List.of(new SupportTicketResponseDTO()));
        when(service.getAllTickets(eq(TicketStatus.OPEN), any(), any(), any(), any(), any(PageRequest.class)))
                .thenReturn(page);

        Page<SupportTicketResponseDTO> result = controller.getAllTickets(
                "OPEN",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                1L,
                2L,
                0,
                10
        );

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("updateTicketResponse delegates")
    void updateTicketResponse_delegates() {
        SupportTicketService service = Mockito.mock(SupportTicketService.class);
        SupportTicketController controller = new SupportTicketController(service);

        SupportTicketResponseDTO dto = new SupportTicketResponseDTO();
        dto.setResponse("ok");
        when(service.updateTicketResponse(1L, "ok")).thenReturn(dto);

        assertThat(controller.updateTicketResponse(1L, "ok").getResponse()).isEqualTo("ok");
    }

    @Test
    @DisplayName("getAllTickets handles invalid status")
    void getAllTickets_invalidStatus_usesNullStatus() {
        SupportTicketService service = Mockito.mock(SupportTicketService.class);
        SupportTicketController controller = new SupportTicketController(service);

        Page<SupportTicketResponseDTO> page = new PageImpl<>(List.of(new SupportTicketResponseDTO()));
        when(service.getAllTickets(isNull(), any(), any(), any(), any(), any(PageRequest.class)))
                .thenReturn(page);

        Page<SupportTicketResponseDTO> result = controller.getAllTickets(
                "NOT_A_STATUS",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                1L,
                2L,
                0,
                10
        );

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("updateTicketStatus rejects invalid enum")
    void updateTicketStatus_invalidEnum_throws() {
        SupportTicketService service = Mockito.mock(SupportTicketService.class);
        SupportTicketController controller = new SupportTicketController(service);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> controller.updateTicketStatus(1L, "BAD_STATUS"));
    }
}
