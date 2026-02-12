package tech.zeta.Digital_Fixed_Deposit_System.controller.withdrawal;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalEligibility;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalHistory;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalPreview;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalReceipt;
import tech.zeta.Digital_Fixed_Deposit_System.service.withdrawal.WithdrawalService;

import java.math.BigDecimal;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/fd")
public class WithdrawalController {

    private WithdrawalService withdrawalService;

    @Autowired
    public void setWithdrawalService(WithdrawalService withdrawalService){

        this.withdrawalService = withdrawalService;

    }

    @GetMapping("/{id}/break-preview")
    public ResponseEntity<WithdrawalPreview> getWithdrawalPreview(@PathVariable Long id, @RequestParam BigDecimal amount){
        log.info("Request received for break preview of fixed deposit Id: {}", id);
        WithdrawalPreview withdrawalPreview = withdrawalService.getWithdrawalPreview(id, amount);
        log.info("Break preview generated successfully sending the preview of fixed deposit Id: {}", id);
        return ResponseEntity.ok(withdrawalPreview);
    }

    @PostMapping("/{id}/break")
    public ResponseEntity<WithdrawalReceipt> confirmWithdrawal(@PathVariable Long id, @RequestParam BigDecimal amount){
        log.info("Request received for confirming withdrawal of fixed deposit Id: {}", id);
        WithdrawalReceipt withdrawalReceipt = withdrawalService.confirmWithdrawal(id, amount);
        log.info("Successfully withdrawn the fixed deposit Id: {} and sending the receipt", id);
        return ResponseEntity.ok(withdrawalReceipt);

    }

    @GetMapping("/{id}/withdrawal-eligibility")
    public ResponseEntity<WithdrawalEligibility> checkWithdrawalEligibility(@PathVariable Long id){
        log.info("Request received for checking the eligibility of fixed deposit Id: {} to withdraw", id);
        WithdrawalEligibility withdrawalEligibility = withdrawalService.checkWithdrawalEligibility(id);
        log.info("Request handled successfully and sending the eligibility of withdrawal summary of fixed deposit Id: {}", id);
        return ResponseEntity.ok(withdrawalEligibility);
    }

    @GetMapping("/{userId}/withdrawals")
    public ResponseEntity<List<WithdrawalHistory>> getWithdrawalsHistory(@PathVariable Long userId){
        List<WithdrawalHistory> withdrawalHistory = withdrawalService.getWithdrawalHistory(userId);
        return ResponseEntity.ok(withdrawalHistory);
    }

}
