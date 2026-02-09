package tech.zeta.Digital_Fixed_Deposit_System.controller.fd;

import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.FDInterestResponse;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.exception.ResourceNotFoundException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.InterestCalculationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/fd")
public class FDInterestController {

    private final FixedDepositRepository fixedDepositRepository;
    private final InterestCalculationService interestCalculationService;
    private final CurrentUserProvider currentUserProvider;


    public FDInterestController(
            FixedDepositRepository fixedDepositRepository,
            InterestCalculationService interestCalculationService,
            CurrentUserProvider currentUserProvider
    ) {
        this.fixedDepositRepository = fixedDepositRepository;
        this.interestCalculationService = interestCalculationService;
        this.currentUserProvider = currentUserProvider;
    }


    // Get current accrued interest for a Fixed Deposit.
    @GetMapping("/{fdId}/interest")
    public ResponseEntity<FDInterestResponse> getAccruedInterest(
            @PathVariable Long fdId
    ) {
        Long userId = currentUserProvider.getCurrentUserId();

        FixedDeposit fd = fixedDepositRepository
                .findByIdAndUserId(fdId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed Deposit not found for user"));

        BigDecimal accruedInterest =
                interestCalculationService.calculateAccruedInterest(fd);

        FDInterestResponse response =
                new FDInterestResponse(
                        fd.getId(),
                        fd.getStatus(),
                        fd.getAmount(),
                        accruedInterest,
                        fd.getInterestRate(),
                        fd.getInterestScheme().getInterestFrequency(),
                        fd.getStartDate(),
                        fd.getMaturityDate()
                );

        return ResponseEntity.ok(response);
    }

}
