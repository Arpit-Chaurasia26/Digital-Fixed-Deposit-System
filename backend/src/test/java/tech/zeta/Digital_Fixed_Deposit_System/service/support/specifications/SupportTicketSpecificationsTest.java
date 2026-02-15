package tech.zeta.Digital_Fixed_Deposit_System.service.support.specifications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.SupportTicket;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;
import tech.zeta.Digital_Fixed_Deposit_System.repository.SupportTicketRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("SupportTicketSpecifications Tests")
class SupportTicketSpecificationsTest {

    @Autowired
    private SupportTicketRepository repository;

    @BeforeEach
    void clearData() {
        repository.deleteAll();
    }

    @Test
    void hasStatus_filtersCorrectly() {
        SupportTicket open = ticket(1L, TicketStatus.OPEN, LocalDateTime.now().minusDays(1));
        SupportTicket closed = ticket(2L, TicketStatus.CLOSED, LocalDateTime.now().minusDays(2));
        repository.save(open);
        repository.save(closed);

        List<SupportTicket> result = repository.findAll(
                SupportTicketSpecifications.hasStatus(TicketStatus.OPEN)
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(TicketStatus.OPEN);
    }

    @Test
    void createdAfter_filtersCorrectly() {
        SupportTicket older = ticket(1L, TicketStatus.OPEN, LocalDateTime.now().minusDays(5));
        SupportTicket newer = ticket(1L, TicketStatus.OPEN, LocalDateTime.now().minusDays(1));
        repository.save(older);
        repository.save(newer);

        List<SupportTicket> result = repository.findAll(
                SupportTicketSpecifications.createdAfter(LocalDateTime.now().minusDays(2))
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCreatedTime()).isAfter(LocalDateTime.now().minusDays(2));
    }

    @Test
    void hasUserId_filtersCorrectly() {
        repository.save(ticket(10L, TicketStatus.OPEN, LocalDateTime.now()));
        repository.save(ticket(20L, TicketStatus.OPEN, LocalDateTime.now()));

        List<SupportTicket> result = repository.findAll(
                SupportTicketSpecifications.hasUserId(10L)
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(10L);
    }

    private SupportTicket ticket(Long userId, TicketStatus status, LocalDateTime created) {
        SupportTicket t = new SupportTicket();
        t.setUserId(userId);
        t.setSubject("subject");
        t.setDescription("desc");
        t.setStatus(status);
        t.setCreatedTime(created);
        t.setUpdatedTime(created);
        return t;
    }
}
