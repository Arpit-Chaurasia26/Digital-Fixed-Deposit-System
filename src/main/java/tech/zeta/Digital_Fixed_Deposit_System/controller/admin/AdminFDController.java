package tech.zeta.Digital_Fixed_Deposit_System.controller.admin;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.*;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositService;

import java.util.List;

@RestController
@RequestMapping("/admin/fd")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFDController {

    private final FixedDepositService fixedDepositService;

    public AdminFDController(FixedDepositService fixedDepositService) {
        this.fixedDepositService = fixedDepositService;
    }

    // Admin can update FD status of a user's FD.
    @PutMapping("/{fdId}/status")
    public ResponseEntity<FixedDeposit> updateFDStatus(
            @PathVariable Long fdId, @Valid @RequestBody UpdateFDStatusRequest request) {
        FixedDeposit updatedFD = fixedDepositService.updateFixedDepositStatus(fdId, request.getStatus());

        return ResponseEntity.ok(updatedFD);
    }


    // Fetch all fixed deposits maturing within N days
    @GetMapping("/maturing")
    public ResponseEntity<List<FDMaturityResponse>> getAllMaturingFDs(@RequestParam(defaultValue = "7") int days) {
        List<FDMaturityResponse> response = fixedDepositService.getAllFDsMaturingWithinDays(days);

        return ResponseEntity.ok(response);
    }

    // Fetch financial year fixed deposit summary analytics for admin
    @GetMapping("/summary/financial-year")
    public ResponseEntity<FDFinancialYearSummaryResponse> getAdminFinancialYearSummary(
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(fixedDepositService.getAdminFinancialYearSummary(year));
    }

    // Fetch all fixed deposits created in a financial year (admin)
    @GetMapping("/yearly")
    public ResponseEntity<List<FixedDeposit>> getFDsByFinancialYear(
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(fixedDepositService.getAdminFDsByFinancialYear(year));
    }

    // Fetch all fixed deposits in chronological order (admin)
    @GetMapping("/all")
    public ResponseEntity<List<FixedDeposit>> getAllFDsChronological(
            @RequestParam(defaultValue = "desc") String order
    ) {
        return ResponseEntity.ok(fixedDepositService.getAllFDsChronological(order));
    }

    // Fetch user fixed deposit portfolio
    @GetMapping("/user/{userId}/portfolio")
    public ResponseEntity<FDPortfolioResponse> getUserPortfolioAsAdmin(@PathVariable Long userId) {
        return ResponseEntity.ok(fixedDepositService.getUserFDPortfolio(userId));
    }

    // Fetch FD interest accrual timeline
    @GetMapping("/{fdId}/interest/timeline")
    public ResponseEntity<FDInterestTimelineResponse> getInterestTimelineAsAdmin(
            @PathVariable Long fdId,
            @RequestParam(required = false) String interval
    ) {
        FixedDeposit fd = fixedDepositService.getFixedDepositByIdForAdmin(fdId);

        String effectiveInterval =
                (interval != null) ? interval : fd.getInterestScheme().getInterestFrequency().name();

        return ResponseEntity.ok(fixedDepositService.getInterestTimeline(fd, effectiveInterval));
    }


}
