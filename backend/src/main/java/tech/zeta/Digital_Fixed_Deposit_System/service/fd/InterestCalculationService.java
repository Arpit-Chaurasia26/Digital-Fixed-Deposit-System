package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

// Author - Arpit Chaurasia
@Service
public class InterestCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(InterestCalculationService.class);

    private final List<InterestCalculationStrategy> strategies;

    public InterestCalculationService(List<InterestCalculationStrategy> strategies) {
        this.strategies = strategies;
    }

    // Author - Arpit Chaurasia
    // Calculates accrued interest till today or till maturity (whichever is earlier).
    public BigDecimal calculateAccruedInterest(FixedDeposit fd) {
        logger.info("Calculating accrued interest: fdId={}, status={}", fd.getId(), fd.getStatus());

        if (fd.getStatus() != FDStatus.ACTIVE &&
                fd.getStatus() != FDStatus.MATURED) {
            logger.debug("Skipping interest calculation due to status: fdId={}, status={}", fd.getId(), fd.getStatus());
            return BigDecimal.ZERO;
        }

        LocalDate startDate = fd.getStartDate();
        LocalDate endDate = determineEndDate(fd);

        if (endDate.isBefore(startDate)) {
            logger.debug("Skipping interest calculation due to date range: fdId={}, start={}, end={}", fd.getId(), startDate, endDate);
            return BigDecimal.ZERO;
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);

        if (totalDays <= 0) {
            logger.debug("Skipping interest calculation due to non-positive duration: fdId={}, totalDays={}", fd.getId(), totalDays);
            return BigDecimal.ZERO;
        }

        BigDecimal principal = fd.getAmount();
        BigDecimal annualRate = fd.getInterestRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        InterestFrequency frequency = fd.getInterestScheme().getInterestFrequency();

        logger.debug("Interest calculation params: fdId={}, principal={}, annualRate={}, frequency={}, totalDays={}",
                    fd.getId(), principal, annualRate, frequency, totalDays);

        BigDecimal interest = resolveStrategy(frequency)
                .calculate(principal, annualRate, startDate, endDate);

        logger.info("Interest calculated: fdId={}, interest={}", fd.getId(), interest);
        return interest.setScale(2, RoundingMode.HALF_UP);
    }

    // Helper Methods

    // Author - Arpit Chaurasia
    private LocalDate determineEndDate(FixedDeposit fd) {
        logger.debug("Determining end date: fdId={}, status={}, maturityDate={}",
                    fd.getId(), fd.getStatus(), fd.getMaturityDate());

        LocalDate today = LocalDate.now();

        if (fd.getStatus() == FDStatus.MATURED ||
                today.isAfter(fd.getMaturityDate())) {
            logger.debug("Using maturity date as end date: fdId={}, endDate={}", fd.getId(), fd.getMaturityDate());
            return fd.getMaturityDate();
        }

        logger.debug("Using today as end date: fdId={}, endDate={}", fd.getId(), today);
        return today;
    }

    // Author - Arpit Chaurasia
    private InterestCalculationStrategy resolveStrategy(InterestFrequency frequency) {
        logger.debug("Resolving interest calculation strategy for frequency: {}", frequency);

        return strategies.stream()
                .filter(strategy -> strategy.supports(frequency))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("No interest calculation strategy found for frequency: {}", frequency);
                    return new IllegalStateException(
                            "No interest calculation strategy for frequency: " + frequency
                    );
                });
    }
}
