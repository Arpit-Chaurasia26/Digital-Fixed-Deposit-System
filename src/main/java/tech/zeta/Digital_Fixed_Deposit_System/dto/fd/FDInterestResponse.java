package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FDInterestResponse {

    private Long fdId;
    private FDStatus status;

    private BigDecimal amount;
    private BigDecimal accruedInterest;
    private BigDecimal interestRate;
    private InterestFrequency interestFrequency;

    private LocalDate startDate;
    private LocalDate maturityDate;

    public FDInterestResponse(
            Long fdId,
            FDStatus status,
            BigDecimal amount,
            BigDecimal accruedInterest,
            BigDecimal interestRate,
            InterestFrequency interestFrequency,
            LocalDate startDate,
            LocalDate maturityDate
    ) {
        this.fdId = fdId;
        this.status = status;
        this.amount = amount;
        this.accruedInterest = accruedInterest;
        this.interestRate = interestRate;
        this.interestFrequency = interestFrequency;
        this.startDate = startDate;
        this.maturityDate = maturityDate;
    }

    public Long getFdId() {
        return fdId;
    }

    public FDStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getAccruedInterest() {
        return accruedInterest;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public InterestFrequency getInterestFrequency() {
        return interestFrequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }
}
