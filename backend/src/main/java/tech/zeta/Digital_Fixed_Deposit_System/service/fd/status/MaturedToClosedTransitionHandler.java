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
public class MaturedToClosedTransitionHandler implements FDStatusTransitionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MaturedToClosedTransitionHandler.class);

    // Author - Arpit Chaurasia
    @Override
    public boolean supports(FDStatus currentStatus, FDStatus targetStatus) {
        return currentStatus == FDStatus.MATURED && targetStatus == FDStatus.CLOSED;
    }

    // Author - Arpit Chaurasia
    @Override
    public void apply(FixedDeposit fd) {
        if (LocalDate.now().isBefore(fd.getMaturityDate())) {
            logger.warn("FD cannot be closed before maturity: fdId={}, maturityDate={}", fd.getId(), fd.getMaturityDate());
            throw new BusinessException("FD cannot be closed before maturity date");
        }
        fd.setStatus(FDStatus.CLOSED);
    }
}
