package tech.zeta.Digital_Fixed_Deposit_System.repository;

import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {

    List<FixedDeposit> findByUserId(Long userId);

    List<FixedDeposit> findByUserIdAndStatus(Long userId, FDStatus status);

    Optional<FixedDeposit> findByIdAndUserId(Long fdId, Long userId);

    List<FixedDeposit> findByStatus(FDStatus status);

    List<FixedDeposit> findByUserIdAndAmountBetween(Long userId, BigDecimal minAmount, BigDecimal maxAmount);

    List<FixedDeposit> findByUserIdAndStatusAndAmountBetween(Long userId, FDStatus status, BigDecimal minAmount, BigDecimal maxAmount);

    List<FixedDeposit> findByUserIdAndMaturityDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    List<FixedDeposit> findByMaturityDateBetween(LocalDate startDate, LocalDate endDate);

    List<FixedDeposit> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    List<FixedDeposit> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
