package tech.zeta.Digital_Fixed_Deposit_System.service.fd.interest;

import org.springframework.stereotype.Component;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Author - Arpit Chaurasia
@Component
public class MonthlySimpleInterestStrategy implements InterestCalculationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(MonthlySimpleInterestStrategy.class);
    private static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");

    // Author - Arpit Chaurasia
    @Override
    public boolean supports(InterestFrequency frequency) {
        return frequency == InterestFrequency.MONTHLY;
    }

    // Author - Arpit Chaurasia
    @Override
    public BigDecimal calculate(BigDecimal principal, BigDecimal annualRate, LocalDate startDate, LocalDate endDate) {
        long wholeMonths = ChronoUnit.MONTHS.between(startDate, endDate);
        BigDecimal months = BigDecimal.valueOf(Math.max(0, wholeMonths));

        logger.debug("Monthly interest calc: start={}, end={}, months={}", startDate, endDate, months);

        BigDecimal monthlyRate =
                annualRate.divide(MONTHS_IN_YEAR, 10, RoundingMode.HALF_UP);

        return principal
                .multiply(monthlyRate)
                .multiply(months);
    }
}
