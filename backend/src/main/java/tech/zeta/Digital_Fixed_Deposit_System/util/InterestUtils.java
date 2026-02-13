package tech.zeta.Digital_Fixed_Deposit_System.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterestUtils {

    private static final Logger logger = LoggerFactory.getLogger(InterestUtils.class);

    public static BigDecimal calculateInterest(BigDecimal principal, BigDecimal interestRate, Long numberOfMonths) {
        logger.debug("Calculating interest: principal={}, annualRate={}, months={}", principal, interestRate, numberOfMonths);

        if (principal == null || interestRate == null || numberOfMonths == null || numberOfMonths <= 0) {
            logger.warn("Invalid parameters for interest calculation: principal={}, annualRate={}, months={}",
                       principal, interestRate, numberOfMonths);
            return BigDecimal.ZERO;
        }

        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(100 * 12), 10, RoundingMode.HALF_UP);
        BigDecimal interest = principal.multiply(monthlyRate).multiply(BigDecimal.valueOf(numberOfMonths));

        logger.debug("Interest calculated: {}", interest);
        return interest.setScale(2, RoundingMode.HALF_UP);
    }
}