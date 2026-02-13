package tech.zeta.Digital_Fixed_Deposit_System.service.fd.interest;

import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;

// Author - Arpit Chaurasia
public interface InterestCalculationStrategy {
    boolean supports(InterestFrequency frequency);

    BigDecimal calculate(BigDecimal principal, BigDecimal annualRate, LocalDate startDate, LocalDate endDate);
}
