package tech.zeta.Digital_Fixed_Deposit_System.service.support.Schedular;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.SupportTicket;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;
import tech.zeta.Digital_Fixed_Deposit_System.repository.SupportTicketRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.zeta.Digital_Fixed_Deposit_System.service.support.SupportTicketServiceImpl;


/*
    Author - Akshaya Siripuram
 */

@Service
public class SupportTicketScheduler {


    // Logger for this service
    private static final Logger logger = LoggerFactory.getLogger(SupportTicketScheduler.class);

    private final SupportTicketRepository repository;

    // X = number of days after which resolved tickets auto-close
    private final int AUTO_CLOSE_DAYS = 7; // change to whatever X you want

    public SupportTicketScheduler(SupportTicketRepository repository) {
        this.repository = repository;
    }

    // Run daily at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void autoCloseResolvedTickets() {
        logger.info("Starting auto-close resolved tickets job");

        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(AUTO_CLOSE_DAYS);
        logger.debug("Auto-closing resolved tickets older than: {}", cutoffTime);

        List<SupportTicket> ticketsToClose = repository
                .findByStatusAndUpdatedTimeBefore(TicketStatus.RESOLVED, cutoffTime);

        logger.info("Found {} resolved tickets to auto-close", ticketsToClose.size());

        for(SupportTicket ticket : ticketsToClose) {
            ticket.setStatus(TicketStatus.CLOSED);
            ticket.setUpdatedTime(LocalDateTime.now());
            repository.save(ticket);
            System.out.println("Auto-closed ticket ID: " + ticket.getId());
        }
    }
}


