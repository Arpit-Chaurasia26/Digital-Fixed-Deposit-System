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
public class ActiveToBrokenTransitionHandler implements FDStatusTransitionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ActiveToBrokenTransitionHandler.class);

    // Author - Arpit Chaurasia
    @Override
    public boolean supports(FDStatus currentStatus, FDStatus targetStatus) {
        return currentStatus == FDStatus.ACTIVE && targetStatus == FDStatus.BROKEN;
    }

    // Author - Arpit Chaurasia
    @Override
    public void apply(FixedDeposit fd) {
        if (!fd.getInterestScheme().isPrematureBreakAllowed()) {
            logger.warn("Premature break not allowed: fdId={}, scheme={}", fd.getId(), fd.getInterestScheme());
            throw new BusinessException("Premature withdrawal is not allowed for this FD scheme");
        }
        if (!LocalDate.now().isBefore(fd.getMaturityDate())) {
            logger.warn("FD already matured, cannot break: fdId={}", fd.getId());
            throw new BusinessException("FD has already matured; use withdrawal instead");
        }
        fd.setStatus(FDStatus.BROKEN);
    }
}
