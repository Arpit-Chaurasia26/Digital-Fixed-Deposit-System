package tech.zeta.Digital_Fixed_Deposit_System.controller.admin;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.*;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositService;

import java.util.List;

// Author - Arpit Chaurasia
@RestController
@RequestMapping("/admin/fd")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFDController {

    private static final Logger logger = LoggerFactory.getLogger(AdminFDController.class);

    private final FixedDepositService fixedDepositService;

    public AdminFDController(FixedDepositService fixedDepositService) {
        this.fixedDepositService = fixedDepositService;
    }

    // Author - Arpit Chaurasia
    // Admin can update FD status of a user's FD.
    @PutMapping("/{fdId}/status")
    public ResponseEntity<FixedDeposit> updateFDStatus(
            @PathVariable Long fdId, @Valid @RequestBody UpdateFDStatusRequest request) {
        logger.info("Admin updating FD status: fdId={}, newStatus={}", fdId, request.getStatus());
        FixedDeposit updatedFD = fixedDepositService.updateFixedDepositStatus(fdId, request.getStatus());
        logger.info("Admin updated FD status: fdId={}, status={}", updatedFD.getId(), updatedFD.getStatus());
        return ResponseEntity.ok(updatedFD);
    }


    // Author - Arpit Chaurasia
    // Fetch all fixed deposits maturing within N days
    @GetMapping("/maturing")
    public ResponseEntity<List<FDMaturityResponse>> getAllMaturingFDs(@RequestParam(defaultValue = "7") int days) {
        logger.info("Admin fetching maturing FDs within days={}", days);
        List<FDMaturityResponse> response = fixedDepositService.getAllFDsMaturingWithinDays(days);
        logger.info("Admin fetched maturing FDs: count={}", response.size());
        return ResponseEntity.ok(response);
    }

    // Author - Arpit Chaurasia
    // Fetch financial year fixed deposit summary analytics for admin
    @GetMapping("/summary/financial-year")
    public ResponseEntity<FDFinancialYearSummaryResponse> getAdminFinancialYearSummary(
            @RequestParam(required = false) Integer year
    ) {
        logger.info("Admin fetching financial year summary: year={}", year);
        return ResponseEntity.ok(fixedDepositService.getAdminFinancialYearSummary(year));
    }

    // Author - Arpit Chaurasia
    // Fetch all fixed deposits created in a financial year (admin)
    @GetMapping("/yearly")
    public ResponseEntity<List<FixedDeposit>> getFDsByFinancialYear(
            @RequestParam Integer year
    ) {
        logger.info("Admin fetching FDs by financial year: year={}", year);
        return ResponseEntity.ok(fixedDepositService.getAdminFDsByFinancialYear(year));
    }

    // Author - Arpit Chaurasia
    // Fetch all fixed deposits in chronological order (admin)
    @GetMapping("/all")
    public ResponseEntity<List<FixedDeposit>> getAllFDsChronological(
            @RequestParam(defaultValue = "desc") String order
    ) {
        logger.info("Admin fetching all FDs chronological: order={}", order);
        return ResponseEntity.ok(fixedDepositService.getAllFDsChronological(order));
    }

    // Author - Arpit Chaurasia
    // Fetch user fixed deposit portfolio
    @GetMapping("/user/{userId}/portfolio")
    public ResponseEntity<FDPortfolioResponse> getUserPortfolioAsAdmin(@PathVariable Long userId) {
        logger.info("Admin fetching user portfolio: userId={}", userId);
        return ResponseEntity.ok(fixedDepositService.getUserFDPortfolio(userId));
    }

    // Author - Arpit Chaurasia
    // Fetch FD interest accrual timeline
    @GetMapping("/{fdId}/interest/timeline")
    public ResponseEntity<FDInterestTimelineResponse> getInterestTimelineAsAdmin(
            @PathVariable Long fdId,
            @RequestParam(required = false) String interval
    ) {
        logger.info("Admin fetching interest timeline: fdId={}, interval={}", fdId, interval);
        FixedDeposit fd = fixedDepositService.getFixedDepositByIdForAdmin(fdId);

        String effectiveInterval =
                (interval != null) ? interval : fd.getInterestScheme().getInterestFrequency().name();

        return ResponseEntity.ok(fixedDepositService.getInterestTimeline(fd, effectiveInterval));
    }

}
