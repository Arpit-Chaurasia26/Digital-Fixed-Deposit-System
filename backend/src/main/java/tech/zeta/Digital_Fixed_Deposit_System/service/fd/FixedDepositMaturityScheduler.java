package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Arpit Chaurasia
 */
@Slf4j
@Component
public class FixedDepositMaturityScheduler {
    
    private final FixedDepositRepository fixedDepositRepository;

    public FixedDepositMaturityScheduler(FixedDepositRepository fixedDepositRepository) {
        this.fixedDepositRepository = fixedDepositRepository;
    }

    /**
     * @author Arpit Chaurasia
     */
    // Runs once every minute to mark eligible FDs as MATURED.
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void markMaturedFDs() {
        log.info("Starting FD maturity scheduler run");

        List<FixedDeposit> activeFDs = fixedDepositRepository.findByStatus(FDStatus.ACTIVE);

        LocalDate today = LocalDate.now();
        int maturedCount = 0;

        for (FixedDeposit fd : activeFDs) {
            if (!today.isBefore(fd.getMaturityDate())) {
                fd.setStatus(FDStatus.MATURED);
                maturedCount++;
            }
        }

        fixedDepositRepository.saveAll(activeFDs);
        log.info("FD maturity scheduler completed: active={}, matured={}", activeFDs.size(), maturedCount);
    }
}
