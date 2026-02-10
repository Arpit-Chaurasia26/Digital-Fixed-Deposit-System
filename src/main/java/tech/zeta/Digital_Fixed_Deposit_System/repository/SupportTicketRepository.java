package tech.zeta.Digital_Fixed_Deposit_System.repository;

import tech.zeta.Digital_Fixed_Deposit_System.entity.support.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByUserId(Long userId);
}

