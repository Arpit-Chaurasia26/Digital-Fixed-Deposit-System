package tech.zeta.Digital_Fixed_Deposit_System.service.fd.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;

import java.time.LocalDate;

// Author - Arpit Chaurasia
@Component
public class ActiveToMaturedTransitionHandler implements FDStatusTransitionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ActiveToMaturedTransitionHandler.class);

    // Author - Arpit Chaurasia
    @Override
    public boolean supports(FDStatus currentStatus, FDStatus targetStatus) {
        return currentStatus == FDStatus.ACTIVE && targetStatus == FDStatus.MATURED;
    }

    // Author - Arpit Chaurasia
    @Override
    public void apply(FixedDeposit fd) {
        if (LocalDate.now().isBefore(fd.getMaturityDate())) {
            logger.warn("FD not yet matured: fdId={}, maturityDate={}", fd.getId(), fd.getMaturityDate());
            throw new BusinessException("FD has not yet reached maturity date");
        }
        fd.setStatus(FDStatus.MATURED);
    }
}
