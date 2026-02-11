package tech.zeta.Digital_Fixed_Deposit_System.service.withdrawal;

import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalEligibility;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalHistory;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalPreview;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalReceipt;
import java.math.BigDecimal;
import java.util.List;


public interface WithdrawalService {

    public WithdrawalPreview getWithdrawalPreview(Long id, BigDecimal amount);
    public WithdrawalReceipt confirmWithdrawal(Long id, BigDecimal amount);
    public WithdrawalEligibility checkWithdrawalEligibility(Long id);
    public List<WithdrawalHistory> getWithdrawalHistory(Long userId);

}
