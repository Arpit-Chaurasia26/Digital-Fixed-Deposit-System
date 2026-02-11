package tech.zeta.Digital_Fixed_Deposit_System.controller.fd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.*;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fd")
public class FixedDepositController {

    private static final Logger logger = LogManager.getLogger(FixedDepositController.class);

    private final FixedDepositService fixedDepositService;
    private final CurrentUserProvider currentUserProvider;

    public FixedDepositController(FixedDepositService fixedDepositService, CurrentUserProvider currentUserProvider) {
        this.fixedDepositService = fixedDepositService;
        this.currentUserProvider = currentUserProvider;
    }

    // Get all available FD schemes (public endpoint)
    @GetMapping("/schemes")
    public ResponseEntity<List<SchemeResponse>> getAllSchemes() {
        logger.info("Fetching all FD schemes");
        List<SchemeResponse> schemes = Arrays.stream(InterestScheme.values())
                .map(scheme -> new SchemeResponse(
                        scheme.name(),
                        scheme.getAnnualInterestRate(),
                        scheme.getTenureInMonths(),
                        scheme.getInterestFrequency().name(),
                        scheme.isPrematureBreakAllowed()
                ))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(schemes);
    }

    // Book a new Fixed Deposit.
    @PostMapping("/book")
    public ResponseEntity<FixedDeposit> bookFixedDeposit(@Valid @RequestBody BookFDRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();
        logger.info("Booking FD: userId={}, amount={}, scheme={}", userId, request.getAmount(), request.getInterestScheme());

        FixedDeposit fd = fixedDepositService.bookFixedDeposit(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fd);
    }

    // Fetch Fixed Deposits of logged-in user (optionally filtered by status and amount range)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FixedDeposit>> getUserFixedDeposits(
            @PathVariable Long userId,
            @RequestParam(required = false) FDStatus status,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount
    ) {
        Long authenticatedUserId = currentUserProvider.getCurrentUserId();
        logger.info("Fetching user FDs: userId={}, status={}, minAmount={}, maxAmount={}", userId, status, minAmount, maxAmount);

        if (!authenticatedUserId.equals(userId)) {
            throw new UnauthorizedException("You are not allowed to access other user's FDs");
        }

        List<FixedDeposit> fds =
                fixedDepositService.getFixedDepositsByFilters(userId, status, minAmount, maxAmount);

        return ResponseEntity.ok(fds);
    }

    // Fetch a specific Fixed Deposit of logged-in user.
    @GetMapping("/user/{userId}/{fdId}")
    public ResponseEntity<FixedDeposit> getFixedDepositById(@PathVariable Long userId, @PathVariable Long fdId) {
        Long authenticatedUserId = currentUserProvider.getCurrentUserId();
        logger.info("Fetching user FD by id: userId={}, fdId={}", userId, fdId);

        if (!authenticatedUserId.equals(userId)) {
            throw new UnauthorizedException("You are not allowed to access other user's FD");
        }

        FixedDeposit fd = fixedDepositService.getFixedDepositById(userId, fdId);

        return ResponseEntity.ok(fd);
    }

    // Fetch fixed deposits maturing within N days
    @GetMapping("/user/{userId}/maturing")
    public ResponseEntity<List<FDMaturityResponse>> getUserMaturingFDs(
            @PathVariable Long userId, @RequestParam(defaultValue = "7") int days) {
        Long authenticatedUserId = currentUserProvider.getCurrentUserId();
        logger.info("Fetching user maturing FDs: userId={}, days={}", userId, days);

        if (!authenticatedUserId.equals(userId)) {
            throw new UnauthorizedException("You are not allowed to access other user's FDs");
        }

        List<FDMaturityResponse> response =
                fixedDepositService.getUserFDsMaturingWithinDays(userId, days);

        return ResponseEntity.ok(response);
    }

    // Fetch financial year fixed deposit summary analytics for user
    @GetMapping("/user/{userId}/summary/financial-year")
    public ResponseEntity<FDFinancialYearSummaryResponse> getUserFinancialYearSummary(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer year
    ) {
        Long authenticatedUserId = currentUserProvider.getCurrentUserId();
        logger.info("Fetching user financial year summary: userId={}, year={}", userId, year);

        if (!authenticatedUserId.equals(userId)) {
            throw new UnauthorizedException("You are not allowed to access other user's summary");
        }

        return ResponseEntity.ok(fixedDepositService.getUserFinancialYearSummary(userId, year));
    }

    // Fetch user fixed deposit portfolio
    @GetMapping("/user/{userId}/portfolio")
    public ResponseEntity<FDPortfolioResponse> getUserFDPortfolio(@PathVariable Long userId) {
        Long authenticatedUserId = currentUserProvider.getCurrentUserId();
        logger.info("Fetching user portfolio: userId={}", userId);

        if (!authenticatedUserId.equals(userId)) {
            throw new UnauthorizedException("You are not allowed to access other user's portfolio");
        }

        return ResponseEntity.ok(fixedDepositService.getUserFDPortfolio(userId));
    }


    // Fetch FD interest accrual timeline
    @GetMapping("/{fdId}/interest/timeline")
    public ResponseEntity<FDInterestTimelineResponse> getInterestTimeline(
            @PathVariable Long fdId,
            @RequestParam(required = false) String interval
    ) {
        Long userId = currentUserProvider.getCurrentUserId();
        logger.info("Fetching interest timeline: userId={}, fdId={}, interval={}", userId, fdId, interval);

        FixedDeposit fd = fixedDepositService.getFixedDepositById(userId, fdId);

        String effectiveInterval =
                (interval != null) ? interval : fd.getInterestScheme().getInterestFrequency().name();

        return ResponseEntity.ok(fixedDepositService.getInterestTimeline(fd, effectiveInterval));
    }

    // Get current accrued interest for a Fixed Deposit.
    @GetMapping("/{fdId}/interest")
    public ResponseEntity<FDInterestResponse> getAccruedInterest(@PathVariable Long fdId) {
        Long userId = currentUserProvider.getCurrentUserId();
        logger.info("Fetching accrued interest: userId={}, fdId={}", userId, fdId);

        return ResponseEntity.ok(fixedDepositService.getCurrentInterestForUser(userId, fdId));
    }

}

