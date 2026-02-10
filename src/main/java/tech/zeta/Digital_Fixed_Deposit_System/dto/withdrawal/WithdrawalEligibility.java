package tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WithdrawalEligibility {

    public WithdrawalEligibility(boolean isEligible, String rootCause) {
        this.isEligible = isEligible;
        this.rootCause = rootCause;
    }

    private boolean isEligible;
    private String rootCause;
}
