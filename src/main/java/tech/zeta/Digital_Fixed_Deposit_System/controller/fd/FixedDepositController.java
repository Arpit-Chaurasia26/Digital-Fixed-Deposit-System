package tech.zeta.Digital_Fixed_Deposit_System.controller.fd;


import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.BookFDRequest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
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


    public FixedDepositController(
            FixedDepositService fixedDepositService,
            CurrentUserProvider currentUserProvider
    ) {
        this.fixedDepositService = fixedDepositService;
        this.currentUserProvider = currentUserProvider;
    }


    // Book a new Fixed Deposit.
    @PostMapping("/book")
    public ResponseEntity<FixedDeposit> bookFixedDeposit(
            @Valid @RequestBody BookFDRequest request
    ) {
        Long userId = currentUserProvider.getCurrentUserId();


        FixedDeposit fd =
                fixedDepositService.bookFixedDeposit(userId, request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fd);
    }


    // Fetch all Fixed Deposits of logged-in user.
    @GetMapping("/user")
    public ResponseEntity<List<FixedDeposit>> getUserFixedDeposits() {


        Long userId = currentUserProvider.getCurrentUserId();


        List<FixedDeposit> fds =
                fixedDepositService.getFixedDepositsByUser(userId);


        return ResponseEntity.ok(fds);
    }


    // Fetch a specific Fixed Deposit of logged-in user.
    @GetMapping("/user/{fdId}")
    public ResponseEntity<FixedDeposit> getFixedDepositById(
            @PathVariable Long fdId
    ) {
        Long userId = currentUserProvider.getCurrentUserId();


        FixedDeposit fd =
                fixedDepositService.getFixedDepositById(userId, fdId);


        return ResponseEntity.ok(fd);
    }


}








