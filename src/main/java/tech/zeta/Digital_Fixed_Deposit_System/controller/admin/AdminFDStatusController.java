package tech.zeta.Digital_Fixed_Deposit_System.controller.admin;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.UpdateFDStatusRequest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositService;

@RestController
@RequestMapping("/fd")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFDStatusController {

    private final FixedDepositService fixedDepositService;

    public AdminFDStatusController(FixedDepositService fixedDepositService) {
        this.fixedDepositService = fixedDepositService;
    }

    // Admin can update FD status of ANY user.
    @PutMapping("/{fdId}/status")
    public ResponseEntity<FixedDeposit> updateFDStatus(
            @PathVariable Long fdId,
            @Valid @RequestBody UpdateFDStatusRequest request
    ) {
        FixedDeposit updatedFD = fixedDepositService.updateFixedDepositStatus(fdId, request.getStatus());

        return ResponseEntity.ok(updatedFD);
    }
}
