package tech.zeta.Digital_Fixed_Deposit_System.service.fd.status;

import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;

// Author - Arpit Chaurasia
public interface FDStatusTransitionHandler {
    boolean supports(FDStatus currentStatus, FDStatus targetStatus);

    void apply(FixedDeposit fd);
}
