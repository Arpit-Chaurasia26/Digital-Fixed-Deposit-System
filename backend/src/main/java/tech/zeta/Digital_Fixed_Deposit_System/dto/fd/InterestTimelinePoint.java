package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import java.math.BigDecimal;

/**
 * @author Arpit Chaurasia
 */
public class InterestTimelinePoint {

    private String period;
    private BigDecimal accruedInterest;

    public InterestTimelinePoint(String period, BigDecimal accruedInterest) {
        this.period = period;
        this.accruedInterest = accruedInterest;
    }

    public String getPeriod() {
        return period;
    }

    public BigDecimal getAccruedInterest() {
        return accruedInterest;
    }
}
