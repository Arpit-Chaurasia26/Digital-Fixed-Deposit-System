package tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


public class WithdrawalPreview {

    private BigDecimal principleAmount;
    private BigDecimal accumulatedInterestAmount;
    private BigDecimal interestRate;
    private BigDecimal penalty;
    private BigDecimal netInterestAmount;

    public WithdrawalPreview(BigDecimal principleAmount, BigDecimal accumulatedInterestAmount, BigDecimal interestRate, BigDecimal penalty, BigDecimal netInterestAmount) {
        this.principleAmount = principleAmount;
        this.accumulatedInterestAmount = accumulatedInterestAmount;
        this.interestRate = interestRate;
        this.penalty = penalty;
        this.netInterestAmount = netInterestAmount;
    }

    public BigDecimal getPrincipleAmount() {
        return principleAmount;
    }

    public void setPrincipleAmount(BigDecimal principleAmount) {
        this.principleAmount = principleAmount;
    }

    public BigDecimal getAccumulatedInterestAmount() {
        return accumulatedInterestAmount;
    }

    public void setAccumulatedInterestAmount(BigDecimal accumulatedInterestAmount) {
        this.accumulatedInterestAmount = accumulatedInterestAmount;
    }

    public BigDecimal getPenalty() {
        return penalty;
    }

    public void setPenalty(BigDecimal penalty) {
        this.penalty = penalty;
    }

    public BigDecimal getNetInterestAmount() {
        return netInterestAmount;
    }

    public void setNetInterestAmount(BigDecimal netInterestAmount) {
        this.netInterestAmount = netInterestAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
