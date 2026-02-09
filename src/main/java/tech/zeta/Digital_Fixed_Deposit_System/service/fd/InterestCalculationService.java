package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class InterestCalculationService {

    private static final BigDecimal DAYS_IN_YEAR = new BigDecimal("365");
    private static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");

    // Calculates accrued interest till today or till maturity (whichever is earlier).
    public BigDecimal calculateAccruedInterest(FixedDeposit fd) {

        if (fd.getStatus() != FDStatus.ACTIVE &&
                fd.getStatus() != FDStatus.MATURED) {
            return BigDecimal.ZERO;
        }

        LocalDate startDate = fd.getStartDate();
        LocalDate endDate = determineEndDate(fd);

        if (endDate.isBefore(startDate)) {
            return BigDecimal.ZERO;
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);

        if (totalDays <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal principal = fd.getAmount();
        BigDecimal annualRate = fd.getInterestRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        BigDecimal interest;

        if (fd.getInterestScheme().getInterestFrequency()
                == InterestFrequency.MONTHLY) {

            interest = calculateMonthlySimpleInterest(
                    principal, annualRate, totalDays
            );

        } else {
            interest = calculateYearlySimpleInterest(
                    principal, annualRate, totalDays
            );
        }

        return interest.setScale(2, RoundingMode.HALF_UP);
    }

    // Helper Methods

    private LocalDate determineEndDate(FixedDeposit fd) {

        LocalDate today = LocalDate.now();

        if (fd.getStatus() == FDStatus.MATURED ||
                today.isAfter(fd.getMaturityDate())) {
            return fd.getMaturityDate();
        }

        return today;
    }

    // Monthly simple interest calculation.
    private BigDecimal calculateMonthlySimpleInterest(
            BigDecimal principal,
            BigDecimal annualRate,
            long totalDays
    ) {
        BigDecimal months =
                BigDecimal.valueOf(totalDays)
                        .divide(BigDecimal.valueOf(30), 10, RoundingMode.HALF_UP);

        BigDecimal monthlyRate =
                annualRate.divide(MONTHS_IN_YEAR, 10, RoundingMode.HALF_UP);

        return principal
                .multiply(monthlyRate)
                .multiply(months);
    }

    // Yearly simple interest calculation.
    private BigDecimal calculateYearlySimpleInterest(
            BigDecimal principal,
            BigDecimal annualRate,
            long totalDays
    ) {
        BigDecimal years =
                BigDecimal.valueOf(totalDays)
                        .divide(DAYS_IN_YEAR, 10, RoundingMode.HALF_UP);

        return principal
                .multiply(annualRate)
                .multiply(years);
    }
}
