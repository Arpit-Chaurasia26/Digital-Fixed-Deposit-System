package tech.zeta.Digital_Fixed_Deposit_System.controller.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalPreview;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalReceipt;
import tech.zeta.Digital_Fixed_Deposit_System.service.withdrawal.WithdrawalService;


@RestController
@RequestMapping("/fd")
public class WithdrawalController {

    private WithdrawalService withdrawalService;

    @Autowired
    public void setWithdrawalService(WithdrawalService withdrawalService){

        this.withdrawalService = withdrawalService;

    }

    @GetMapping("/{id}/break-preview")
    public ResponseEntity<WithdrawalPreview> getWithdrawalPreview(@PathVariable Long id){
        System.out.println("Controller Called");
        WithdrawalPreview withdrawalPreview = withdrawalService.getWithdrawalPreview(id);
        return ResponseEntity.ok(withdrawalPreview);

    }

    @PostMapping("/{id}/break")
    public ResponseEntity<WithdrawalReceipt> confirmWithdrawal(@PathVariable Long id){

        WithdrawalReceipt withdrawalReceipt = withdrawalService.confirmWithdrawal(id);
        return ResponseEntity.ok(withdrawalReceipt);

    }

    @GetMapping("/{id}/withdrawal-eligibility")
    public ResponseEntity<Boolean> confirmationWithdrawal(@PathVariable Long id){
        return null;
    }


}
