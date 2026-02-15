package tech.zeta.Digital_Fixed_Deposit_System.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InterestUtils {

    public static BigDecimal calculateInterest(BigDecimal principal, BigDecimal interestRate, Long numberOfMonths) {
        log.debug("Calculating interest: principal={}, annualRate={}, months={}", principal, interestRate, numberOfMonths);

        if (principal == null || interestRate == null || numberOfMonths == null || numberOfMonths <= 0) {
            log.warn("Invalid parameters for interest calculation: principal={}, annualRate={}, months={}",
                       principal, interestRate, numberOfMonths);
            return BigDecimal.ZERO;
        }

        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(100 * 12), 10, RoundingMode.HALF_UP);
        BigDecimal interest = principal.multiply(monthlyRate).multiply(BigDecimal.valueOf(numberOfMonths));

        log.debug("Interest calculated: {}", interest);
        return interest.setScale(2, RoundingMode.HALF_UP);
    }
}