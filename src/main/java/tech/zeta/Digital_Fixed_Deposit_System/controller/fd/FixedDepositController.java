package tech.zeta.Digital_Fixed_Deposit_System.controller.fd;

import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.BookFDRequest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    // Fetch Fixed Deposits of logged-in user (optionally filtered by status)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FixedDeposit>> getUserFixedDeposits(
            @PathVariable Long userId, @RequestParam(required = false) FDStatus status) {
        Long authenticatedUserId = currentUserProvider.getCurrentUserId();

        if (!authenticatedUserId.equals(userId)) {
            throw new UnauthorizedException("You are not allowed to access other user's FDs");
        }

        List<FixedDeposit> fds = fixedDepositService.getFixedDepositsByStatus(userId, status);

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
}

