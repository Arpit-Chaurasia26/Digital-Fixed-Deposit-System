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
public class ActiveToMaturedTransitionHandler implements FDStatusTransitionHandler {

    /**
     * @author Arpit Chaurasia
     */
    @Override
    public boolean supports(FDStatus currentStatus, FDStatus targetStatus) {
        return currentStatus == FDStatus.ACTIVE && targetStatus == FDStatus.MATURED;
    }

    /**
     * @author Arpit Chaurasia
     */
    @Override
    public void apply(FixedDeposit fd) {
        if (LocalDate.now().isBefore(fd.getMaturityDate())) {
            log.warn("FD not yet matured: fdId={}, maturityDate={}", fd.getId(), fd.getMaturityDate());
            throw new BusinessException("FD has not yet reached maturity date");
        }
        fd.setStatus(FDStatus.MATURED);
    }
}
