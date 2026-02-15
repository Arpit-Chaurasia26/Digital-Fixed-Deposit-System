package tech.zeta.Digital_Fixed_Deposit_System.entity.fd;

import java.math.BigDecimal;

/**
 * @author Arpit Chaurasia
 */
public enum InterestScheme {

    STANDARD_6_MONTHS(
            new BigDecimal("5.50"),
            6,
            InterestFrequency.MONTHLY,
            true
    ),

    STANDARD_12_MONTHS(
            new BigDecimal("6.50"),
            12,
            InterestFrequency.MONTHLY,
            true
    ),

    SENIOR_CITIZEN_12_MONTHS(
            new BigDecimal("7.25"),
            12,
            InterestFrequency.MONTHLY,
            true
    ),

    TAX_SAVER_5_YEARS(
            new BigDecimal("7.00"),
            60,
            InterestFrequency.YEARLY,
            false
    );

    private final BigDecimal annualInterestRate;
    private final int tenureInMonths;
    private final InterestFrequency interestFrequency;
    private final boolean prematureBreakAllowed;

    InterestScheme(
            BigDecimal annualInterestRate,
            int tenureInMonths,
            InterestFrequency interestFrequency,
            boolean prematureBreakAllowed
    ) {
        this.annualInterestRate = annualInterestRate;
        this.tenureInMonths = tenureInMonths;
        this.interestFrequency = interestFrequency;
        this.prematureBreakAllowed = prematureBreakAllowed;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public int getTenureInMonths() {
        return tenureInMonths;
    }

    public InterestFrequency getInterestFrequency() {
        return interestFrequency;
    }

    public boolean isPrematureBreakAllowed() {
        return prematureBreakAllowed;
    }
}
