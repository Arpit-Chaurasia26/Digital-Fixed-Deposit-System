package tech.zeta.Digital_Fixed_Deposit_System.service.withdrawal;

import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalEligibility;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalPreview;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalReceipt;


public interface WithdrawalService {

    public WithdrawalPreview getWithdrawalPreview(Long id);
    public WithdrawalReceipt confirmWithdrawal(Long id);
    public WithdrawalEligibility checkWithdrawalEligibility(Long id);

}
