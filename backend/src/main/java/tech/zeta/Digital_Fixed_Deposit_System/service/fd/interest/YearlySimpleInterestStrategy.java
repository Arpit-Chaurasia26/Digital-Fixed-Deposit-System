package tech.zeta.Digital_Fixed_Deposit_System.service.fd.interest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * @author Arpit Chaurasia
 */
@Slf4j
@Component
public class YearlySimpleInterestStrategy implements InterestCalculationStrategy {

    @Override
    public boolean supports(InterestFrequency frequency) {
        return frequency == InterestFrequency.YEARLY;
    }

    /**
     * @author Arpit Chaurasia
     */
    @Override
    public BigDecimal calculate(BigDecimal principal, BigDecimal annualRate, LocalDate startDate, LocalDate endDate) {
        long wholeYears = ChronoUnit.YEARS.between(startDate, endDate);
        BigDecimal years = BigDecimal.valueOf(Math.max(0, wholeYears));

        log.debug("Yearly interest calc: start={}, end={}, years={}", startDate, endDate, years);

        return principal
                .multiply(annualRate)
                .multiply(years);
    }
}
