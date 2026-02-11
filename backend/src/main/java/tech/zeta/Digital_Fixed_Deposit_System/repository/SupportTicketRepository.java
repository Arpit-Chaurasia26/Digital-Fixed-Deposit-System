package tech.zeta.Digital_Fixed_Deposit_System.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import tech.zeta.Digital_Fixed_Deposit_System.entity.support.SupportTicket;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long>, JpaSpecificationExecutor<SupportTicket> {

    // Custom query method
    List<SupportTicket> findByUserId(Long userId);

    // Find resolved tickets which were updated before the cutoff date
    List<SupportTicket> findByStatusAndUpdatedTimeBefore(TicketStatus status, LocalDateTime cutoff);
}


