package tech.zeta.Digital_Fixed_Deposit_System.repository;


import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;


@Repository
public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {

    List<FixedDeposit> findByUserId(Long userId);

    Optional<FixedDeposit> findByIdAndUserId(Long fdId, Long userId);
}

