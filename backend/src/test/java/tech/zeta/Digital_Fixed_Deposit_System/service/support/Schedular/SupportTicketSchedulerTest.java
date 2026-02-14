package tech.zeta.Digital_Fixed_Deposit_System.service.support.Schedular;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.SupportTicket;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;
import tech.zeta.Digital_Fixed_Deposit_System.repository.SupportTicketRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("SupportTicketScheduler Unit Tests")
class SupportTicketSchedulerTest {

    @Test
    void autoCloseResolvedTickets_closesEligibleTickets() {
        SupportTicketRepository repository = mock(SupportTicketRepository.class);
        SupportTicketScheduler scheduler = new SupportTicketScheduler(repository);

        SupportTicket ticket = new SupportTicket();
        ticket.setId(1L);
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setUpdatedTime(LocalDateTime.now().minusDays(10));

        when(repository.findByStatusAndUpdatedTimeBefore(eq(TicketStatus.RESOLVED), any()))
                .thenReturn(List.of(ticket));

        scheduler.autoCloseResolvedTickets();

        ArgumentCaptor<SupportTicket> captor = ArgumentCaptor.forClass(SupportTicket.class);
        verify(repository, atLeastOnce()).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(TicketStatus.CLOSED);
    }
}

