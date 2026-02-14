package tech.zeta.Digital_Fixed_Deposit_System.entity.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SupportTicket Entity Tests")
class SupportTicketTest {

    @Test
    void gettersAndSetters_work() {
        SupportTicket ticket = new SupportTicket();
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        LocalDateTime updated = LocalDateTime.now();

        ticket.setId(1L);
        ticket.setUserId(2L);
        ticket.setFdId(3L);
        ticket.setSubject("subject");
        ticket.setDescription("desc");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setResponse("response");
        ticket.setCreatedTime(created);
        ticket.setUpdatedTime(updated);

        assertThat(ticket.getId()).isEqualTo(1L);
        assertThat(ticket.getUserId()).isEqualTo(2L);
        assertThat(ticket.getFdId()).isEqualTo(3L);
        assertThat(ticket.getSubject()).isEqualTo("subject");
        assertThat(ticket.getDescription()).isEqualTo("desc");
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.OPEN);
        assertThat(ticket.getResponse()).isEqualTo("response");
        assertThat(ticket.getCreatedTime()).isEqualTo(created);
        assertThat(ticket.getUpdatedTime()).isEqualTo(updated);
    }
}

