package tech.zeta.Digital_Fixed_Deposit_System.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.zeta.Digital_Fixed_Deposit_System.entity.transaction.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long UserId);
}
