package tech.zeta.Digital_Fixed_Deposit_System.controller.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalPreview;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalReceipt;
import tech.zeta.Digital_Fixed_Deposit_System.service.withdrawal.WithdrawalService;


@RestController
@RequestMapping("/")
public class WithdrawalController {

    private WithdrawalService withdrawalService;

    @Autowired
    public void setWithdrawalService(WithdrawalService withdrawalService){

        this.withdrawalService = withdrawalService;

    }

    @PostMapping("/{id}/withdrawal-preview")
    public ResponseEntity<WithdrawalPreview> getWithdrawalPreview(@PathVariable int id){
        System.out.println("Controller Called");
        WithdrawalPreview withdrawalPreview = withdrawalService.getWithdrawalPreview(id);
        return ResponseEntity.ok(withdrawalPreview);

    }

    @PostMapping("/{id}/withdrawal-confirmation")
    public ResponseEntity<WithdrawalReceipt> confirmWithdrawal(@PathVariable int id){

        WithdrawalReceipt withdrawalReceipt = withdrawalService.confirmWithdrawal(id);
        return ResponseEntity.ok(withdrawalReceipt);

    }


}
