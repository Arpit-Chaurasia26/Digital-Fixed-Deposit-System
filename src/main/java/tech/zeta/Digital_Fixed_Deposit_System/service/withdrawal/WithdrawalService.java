package tech.zeta.Digital_Fixed_Deposit_System.service.withdrawal;

import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalPreview;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalReceipt;


public interface WithdrawalService {

    public WithdrawalPreview getWithdrawalPreview(int id);
    public WithdrawalReceipt confirmWithdrawal(int id);

}
