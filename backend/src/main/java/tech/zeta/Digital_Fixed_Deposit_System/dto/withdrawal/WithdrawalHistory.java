package tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
public class WithdrawalHistory {
    private Long fdId;
    private BigDecimal withdrawalAmount;
    private BigDecimal interestPaid;
}
