package tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class WithdrawalReceipt {
    private long id;
    private BigDecimal withdrawalAmount;
    private BigDecimal accruedInterest;
    private BigDecimal interestRate;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private LocalDate closureDate;
}
