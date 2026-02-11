package tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WithdrawalReceipt {
    private long id;
    private BigDecimal principleAmount;
    private BigDecimal accruedInterest;
    private BigDecimal interestRate;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private LocalDate closureDate;

    public WithdrawalReceipt(long id, BigDecimal principleAmount, BigDecimal accruedInterest,
                             BigDecimal interestRate, LocalDate startDate, LocalDate maturityDate,
                             LocalDate closureDate) {
        this.id = id;
        this.principleAmount = principleAmount;
        this.accruedInterest = accruedInterest;
        this.interestRate = interestRate;
        this.startDate = startDate;
        this.maturityDate = maturityDate;
        this.closureDate = closureDate;
    }

    public LocalDate getClosureDate() {
        return closureDate;
    }

    public void setClosureDate(LocalDate closureDate) {
        this.closureDate = closureDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getAccruedInterest() {
        return accruedInterest;
    }

    public void setAccruedInterest(BigDecimal accruedInterest) {
        this.accruedInterest = accruedInterest;
    }

    public BigDecimal getPrincipleAmount() {
        return principleAmount;
    }

    public void setPrincipleAmount(BigDecimal principleAmount) {
        this.principleAmount = principleAmount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
