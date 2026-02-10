package tech.zeta.Digital_Fixed_Deposit_System.controller.fd;

import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.BookFDRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.FDFinancialYearSummaryResponse;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.FDMaturityResponse;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/fd")
public class FixedDepositController {

    private final FixedDepositService fixedDepositService;
    private final CurrentUserProvider currentUserProvider;

    public FixedDepositController(FixedDepositService fixedDepositService, CurrentUserProvider currentUserProvider) {
        this.fixedDepositService = fixedDepositService;
        this.currentUserProvider = currentUserProvider;
    }

    // Book a new Fixed Deposit.
    @PostMapping("/book")
    public ResponseEntity<FixedDeposit> bookFixedDeposit(@Valid @RequestBody BookFDRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();

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

        if (!authenticatedUserId.equals(userId)) {
            throw new UnauthorizedException("You are not allowed to access other user's summary");
        }

        return ResponseEntity.ok(fixedDepositService.getUserFinancialYearSummary(userId, year));
    }


}

