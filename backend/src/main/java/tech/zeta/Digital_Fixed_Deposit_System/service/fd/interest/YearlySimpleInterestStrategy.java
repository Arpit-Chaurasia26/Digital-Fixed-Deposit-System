package tech.zeta.Digital_Fixed_Deposit_System.service.fd.interest;

import org.springframework.stereotype.Component;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Author - Arpit Chaurasia
@Component
public class YearlySimpleInterestStrategy implements InterestCalculationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(YearlySimpleInterestStrategy.class);

    @Override
    public boolean supports(InterestFrequency frequency) {
        return frequency == InterestFrequency.YEARLY;
    }

    // Author - Arpit Chaurasia
    @Override
    public BigDecimal calculate(BigDecimal principal, BigDecimal annualRate, LocalDate startDate, LocalDate endDate) {
        long wholeYears = ChronoUnit.YEARS.between(startDate, endDate);
        BigDecimal years = BigDecimal.valueOf(Math.max(0, wholeYears));

        logger.debug("Yearly interest calc: start={}, end={}, years={}", startDate, endDate, years);

        return principal
                .multiply(annualRate)
                .multiply(years);
    }
}
