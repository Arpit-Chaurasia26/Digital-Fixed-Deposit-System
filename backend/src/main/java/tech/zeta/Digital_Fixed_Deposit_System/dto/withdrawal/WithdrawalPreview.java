package tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * @author Pavan Kalloji
 */
@Getter
@Setter
@AllArgsConstructor
public class WithdrawalPreview {
    private BigDecimal withdrawalAmount;
    private BigDecimal accumulatedInterestAmount;
    private BigDecimal interestRate;
    private BigDecimal penalty;
    private BigDecimal netInterestAmount;
    private BigDecimal balanceAmount;
}
