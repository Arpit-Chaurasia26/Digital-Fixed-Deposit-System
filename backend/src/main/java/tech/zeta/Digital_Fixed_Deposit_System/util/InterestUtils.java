package tech.zeta.Digital_Fixed_Deposit_System.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InterestUtils{
    public static BigDecimal calculateInterest(BigDecimal principal, BigDecimal interestRate, Long numberOfMonths){
        return principal.multiply(new BigDecimal(numberOfMonths)).multiply(interestRate).divide(new BigDecimal(1200), 2, RoundingMode.HALF_UP);
    }
}