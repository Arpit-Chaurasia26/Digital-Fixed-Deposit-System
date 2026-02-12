package tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class WithdrawalEligibility {
    private boolean isEligible;
    private String rootCause;
}
 