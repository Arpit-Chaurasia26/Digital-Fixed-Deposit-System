package tech.zeta.Digital_Fixed_Deposit_System.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;

public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Integer> {

}