package tech.zeta.Digital_Fixed_Deposit_System.service.support.Schedular;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.SupportTicket;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;
import tech.zeta.Digital_Fixed_Deposit_System.repository.SupportTicketRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupportTicketScheduler {

    private final SupportTicketRepository repository;

    // X = number of days after which resolved tickets auto-close
    private final int AUTO_CLOSE_DAYS = 7; // change to whatever X you want

    public SupportTicketScheduler(SupportTicketRepository repository) {
        this.repository = repository;
    }

    // 🔹 Run daily at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void autoCloseResolvedTickets() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(AUTO_CLOSE_DAYS);

        List<SupportTicket> ticketsToClose = repository.findByStatusAndUpdatedTimeBefore(TicketStatus.RESOLVED, cutoff);

        for(SupportTicket ticket : ticketsToClose) {
            ticket.setStatus(TicketStatus.CLOSED);
            ticket.setUpdatedTime(LocalDateTime.now());
            repository.save(ticket);
            System.out.println("Auto-closed ticket ID: " + ticket.getId());
        }
    }
}

