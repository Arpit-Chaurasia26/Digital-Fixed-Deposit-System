package tech.zeta.Digital_Fixed_Deposit_System.controller.admin;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.*;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositService;

import java.util.List;

/**
 * @author Arpit Chaurasia
 */
@Slf4j
@RestController
@RequestMapping("/admin/fd")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFDController {
    
    private final FixedDepositService fixedDepositService;

    public AdminFDController(FixedDepositService fixedDepositService) {
        this.fixedDepositService = fixedDepositService;
    }

    /**
     * @author Arpit Chaurasia
     */
    // Admin can update FD status of a user's FD.
    @PutMapping("/{fdId}/status")
    public ResponseEntity<FixedDeposit> updateFDStatus(
            @PathVariable Long fdId, @Valid @RequestBody UpdateFDStatusRequest request) {
        log.info("Admin updating FD status: fdId={}, newStatus={}", fdId, request.getStatus());
        FixedDeposit updatedFD = fixedDepositService.updateFixedDepositStatus(fdId, request.getStatus());
        log.info("Admin updated FD status: fdId={}, status={}", updatedFD.getId(), updatedFD.getStatus());
        return ResponseEntity.ok(updatedFD);
    }


    /**
     * @author Arpit Chaurasia
     */
    // Fetch all fixed deposits maturing within N days
    @GetMapping("/maturing")
    public ResponseEntity<List<FDMaturityResponse>> getAllMaturingFDs(@RequestParam(defaultValue = "7") int days) {
        log.info("Admin fetching maturing FDs within days={}", days);
        List<FDMaturityResponse> response = fixedDepositService.getAllFDsMaturingWithinDays(days);
        log.info("Admin fetched maturing FDs: count={}", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch financial year fixed deposit summary analytics for admin
    @GetMapping("/summary/financial-year")
    public ResponseEntity<FDFinancialYearSummaryResponse> getAdminFinancialYearSummary(
            @RequestParam(required = false) Integer year
    ) {
        log.info("Admin fetching financial year summary: year={}", year);
        return ResponseEntity.ok(fixedDepositService.getAdminFinancialYearSummary(year));
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch all fixed deposits created in a financial year (admin)
    @GetMapping("/yearly")
    public ResponseEntity<List<FixedDeposit>> getFDsByFinancialYear(
            @RequestParam Integer year
    ) {
        log.info("Admin fetching FDs by financial year: year={}", year);
        return ResponseEntity.ok(fixedDepositService.getAdminFDsByFinancialYear(year));
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch all fixed deposits in chronological order (admin)
    @GetMapping("/all")
    public ResponseEntity<List<FixedDeposit>> getAllFDsChronological(
            @RequestParam(defaultValue = "desc") String order
    ) {
        log.info("Admin fetching all FDs chronological: order={}", order);
        return ResponseEntity.ok(fixedDepositService.getAllFDsChronological(order));
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch a specific fixed deposit by id (admin)
    @GetMapping("/{fdId}")
    public ResponseEntity<FixedDeposit> getFixedDepositByIdAsAdmin(@PathVariable Long fdId) {
        log.info("Admin fetching FD by id: fdId={}", fdId);
        return ResponseEntity.ok(fixedDepositService.getFixedDepositByIdForAdmin(fdId));
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch user fixed deposit portfolio
    @GetMapping("/user/{userId}/portfolio")
    public ResponseEntity<FDPortfolioResponse> getUserPortfolioAsAdmin(@PathVariable Long userId) {
        log.info("Admin fetching user portfolio: userId={}", userId);
        return ResponseEntity.ok(fixedDepositService.getUserFDPortfolio(userId));
    }

    /**
     * @author Arpit Chaurasia
     */
    // Fetch FD interest accrual timeline
    @GetMapping("/{fdId}/interest/timeline")
    public ResponseEntity<FDInterestTimelineResponse> getInterestTimelineAsAdmin(
            @PathVariable Long fdId,
            @RequestParam(required = false) String interval
    ) {
        log.info("Admin fetching interest timeline: fdId={}, interval={}", fdId, interval);
        FixedDeposit fd = fixedDepositService.getFixedDepositByIdForAdmin(fdId);

        String effectiveInterval =
                (interval != null) ? interval : fd.getInterestScheme().getInterestFrequency().name();

        return ResponseEntity.ok(fixedDepositService.getInterestTimeline(fd, effectiveInterval));
    }

}
