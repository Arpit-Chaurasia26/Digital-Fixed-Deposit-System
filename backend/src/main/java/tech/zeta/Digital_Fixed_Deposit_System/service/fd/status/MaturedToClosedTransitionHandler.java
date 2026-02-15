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
public class MaturedToClosedTransitionHandler implements FDStatusTransitionHandler {
    
    /**
     * @author Arpit Chaurasia
     */
    @Override
    public boolean supports(FDStatus currentStatus, FDStatus targetStatus) {
        return currentStatus == FDStatus.MATURED && targetStatus == FDStatus.CLOSED;
    }

    /**
     * @author Arpit Chaurasia
     */
    @Override
    public void apply(FixedDeposit fd) {
        if (LocalDate.now().isBefore(fd.getMaturityDate())) {
            log.warn("FD cannot be closed before maturity: fdId={}, maturityDate={}", fd.getId(), fd.getMaturityDate());
            throw new BusinessException("FD cannot be closed before maturity date");
        }
        fd.setStatus(FDStatus.CLOSED);
    }
}
