package tech.zeta.Digital_Fixed_Deposit_System.service.fd.status;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;

import java.time.LocalDate;

/**
 * @author Arpit Chaurasia
 */
@Slf4j
@Component
public class ActiveToBrokenTransitionHandler implements FDStatusTransitionHandler {

    /**
     * @author Arpit Chaurasia
     */
    @Override
    public boolean supports(FDStatus currentStatus, FDStatus targetStatus) {
        return currentStatus == FDStatus.ACTIVE && targetStatus == FDStatus.BROKEN;
    }

    /**
     * @author Arpit Chaurasia
     */
    @Override
    public void apply(FixedDeposit fd) {
        if (!fd.getInterestScheme().isPrematureBreakAllowed()) {
            log.warn("Premature break not allowed: fdId={}, scheme={}", fd.getId(), fd.getInterestScheme());
            throw new BusinessException("Premature withdrawal is not allowed for this FD scheme");
        }
        if (!LocalDate.now().isBefore(fd.getMaturityDate())) {
            log.warn("FD already matured, cannot break: fdId={}", fd.getId());
            throw new BusinessException("FD has already matured; use withdrawal instead");
        }
        fd.setStatus(FDStatus.BROKEN);
    }
}
