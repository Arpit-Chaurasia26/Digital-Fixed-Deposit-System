package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import java.math.BigDecimal;

public record SchemeResponse(
        String name,
        BigDecimal annualInterestRate,
        int tenureInMonths,
        String interestFrequency,
        boolean prematureBreakAllowed
) {
}
