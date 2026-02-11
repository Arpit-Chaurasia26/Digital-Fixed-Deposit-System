package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;

import java.time.LocalDate;
import java.util.List;

@Component
public class FixedDepositMaturityScheduler {

    private static final Logger logger = LogManager.getLogger(FixedDepositMaturityScheduler.class);

    private final FixedDepositRepository fixedDepositRepository;

    public FixedDepositMaturityScheduler(FixedDepositRepository fixedDepositRepository) {
        this.fixedDepositRepository = fixedDepositRepository;
    }

    // Runs daily at 01:00 AM to mark eligible FDs as MATURED.
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void markMaturedFDs() {
        logger.info("Starting FD maturity scheduler run");

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
        logger.info("FD maturity scheduler completed: active={}, matured={}", activeFDs.size(), maturedCount);
    }
}
