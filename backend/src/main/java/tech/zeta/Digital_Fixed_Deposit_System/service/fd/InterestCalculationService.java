package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import lombok.extern.slf4j.Slf4j;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.interest.InterestCalculationStrategy;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author Arpit Chaurasia
 */
@Slf4j
@Service
public class InterestCalculationService {

    private final List<InterestCalculationStrategy> strategies;

    public InterestCalculationService(List<InterestCalculationStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * @author Arpit Chaurasia
     */
    // Calculates accrued interest till today or till maturity (whichever is earlier).
    public BigDecimal calculateAccruedInterest(FixedDeposit fd) {
        log.info("Calculating accrued interest: fdId={}, status={}", fd.getId(), fd.getStatus());

        if (fd.getStatus() != FDStatus.ACTIVE &&
                fd.getStatus() != FDStatus.MATURED) {
            log.debug("Skipping interest calculation due to status: fdId={}, status={}", fd.getId(), fd.getStatus());
            return BigDecimal.ZERO;
        }

        LocalDate startDate = fd.getStartDate();
        LocalDate endDate = determineEndDate(fd);

        if (endDate.isBefore(startDate)) {
            log.debug("Skipping interest calculation due to date range: fdId={}, start={}, end={}", fd.getId(), startDate, endDate);
            return BigDecimal.ZERO;
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);

        if (totalDays <= 0) {
            log.debug("Skipping interest calculation due to non-positive duration: fdId={}, totalDays={}", fd.getId(), totalDays);
            return BigDecimal.ZERO;
        }

        BigDecimal principal = fd.getAmount();
        BigDecimal annualRate = fd.getInterestRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        InterestFrequency frequency = fd.getInterestScheme().getInterestFrequency();

        log.debug("Interest calculation params: fdId={}, principal={}, annualRate={}, frequency={}, totalDays={}",
                    fd.getId(), principal, annualRate, frequency, totalDays);

        BigDecimal interest = resolveStrategy(frequency)
                .calculate(principal, annualRate, startDate, endDate);

        log.info("Interest calculated: fdId={}, interest={}", fd.getId(), interest);
        return interest.setScale(2, RoundingMode.HALF_UP);
    }

    // Helper Methods

    /**
     * @author Arpit Chaurasia
     */
    private LocalDate determineEndDate(FixedDeposit fd) {
        log.debug("Determining end date: fdId={}, status={}, maturityDate={}",
                    fd.getId(), fd.getStatus(), fd.getMaturityDate());

        LocalDate today = LocalDate.now();

        if (fd.getStatus() == FDStatus.MATURED ||
                today.isAfter(fd.getMaturityDate())) {
            log.debug("Using maturity date as end date: fdId={}, endDate={}", fd.getId(), fd.getMaturityDate());
            return fd.getMaturityDate();
        }

        log.debug("Using today as end date: fdId={}, endDate={}", fd.getId(), today);
        return today;
    }

    /**
     * @author Arpit Chaurasia
     */
    private InterestCalculationStrategy resolveStrategy(InterestFrequency frequency) {
        log.debug("Resolving interest calculation strategy for frequency: {}", frequency);

        return strategies.stream()
                .filter(strategy -> strategy.supports(frequency))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("No interest calculation strategy found for frequency: {}", frequency);
                    return new IllegalStateException(
                            "No interest calculation strategy for frequency: " + frequency
                    );
                });
    }
}
