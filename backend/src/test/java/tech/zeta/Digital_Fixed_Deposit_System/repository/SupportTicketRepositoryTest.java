package tech.zeta.Digital_Fixed_Deposit_System.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.SupportTicket;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("SupportTicketRepository Tests")
class SupportTicketRepositoryTest {

    @Autowired
    private SupportTicketRepository repository;

    @BeforeEach
    void clearData() {
        repository.deleteAll();
    }

    @Test
    void findByUserId_returnsTickets() {
        repository.save(ticket(1L, TicketStatus.OPEN, LocalDateTime.now().minusDays(1)));
        repository.save(ticket(2L, TicketStatus.OPEN, LocalDateTime.now().minusDays(1)));

        assertThat(repository.findByUserId(1L)).hasSize(1);
    }

    @Test
    void findByStatusAndUpdatedTimeBefore_filters() {
        SupportTicket oldResolved = ticket(1L, TicketStatus.RESOLVED, LocalDateTime.now().minusDays(10));
        SupportTicket newResolved = ticket(1L, TicketStatus.RESOLVED, LocalDateTime.now().minusDays(1));
        repository.save(oldResolved);
        repository.save(newResolved);

        assertThat(repository.findByStatusAndUpdatedTimeBefore(TicketStatus.RESOLVED, LocalDateTime.now().minusDays(5)))
                .hasSize(1);
    }

    private SupportTicket ticket(Long userId, TicketStatus status, LocalDateTime time) {
        SupportTicket t = new SupportTicket();
        t.setUserId(userId);
        t.setSubject("subject");
        t.setDescription("desc");
        t.setStatus(status);
        t.setCreatedTime(time);
        t.setUpdatedTime(time);
        return t;
    }
}
