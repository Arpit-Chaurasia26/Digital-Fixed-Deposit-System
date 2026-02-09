package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.BookFDRequest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FixedDepositService {

    private static final BigDecimal MIN_FD_AMOUNT = new BigDecimal("1000");

    private final FixedDepositRepository fixedDepositRepository;
    private final InterestCalculationService interestCalculationService;

    public FixedDepositService(
            FixedDepositRepository fixedDepositRepository,
            InterestCalculationService interestCalculationService
    ) {
        this.fixedDepositRepository = fixedDepositRepository;
        this.interestCalculationService = interestCalculationService;
    }

    // Book a new Fixed Deposit.
    @Transactional
    public FixedDeposit bookFixedDeposit(Long userId, BookFDRequest request) {

        validateAmount(request.getAmount());

        InterestScheme scheme = request.getInterestScheme();
        if (scheme == null) {
            throw new BusinessException("Interest scheme must be selected");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate maturityDate = startDate.plusMonths(scheme.getTenureInMonths());

        FixedDeposit fd = new FixedDeposit();

        fd.setUserId(userId);
        fd.setAmount(request.getAmount());
        fd.setInterestScheme(scheme);
        fd.setInterestRate(scheme.getAnnualInterestRate());
        fd.setTenureMonths(scheme.getTenureInMonths());
        fd.setStartDate(startDate);
        fd.setMaturityDate(maturityDate);
        fd.setStatus(FDStatus.ACTIVE);
        fd.setAccruedInterest(BigDecimal.ZERO);

        return fixedDepositRepository.save(fd);
    }

    // Fetch all Fixed Deposits of a user with dynamically calculated accrued interest.
    @Transactional(readOnly = true)
    public List<FixedDeposit> getFixedDepositsByUser(Long userId) {

        List<FixedDeposit> fds = fixedDepositRepository.findByUserId(userId);

        fds.forEach(this::enrichWithAccruedInterest);

        return fds;
    }

    // Fetch a specific Fixed Deposit of a user.
    @Transactional(readOnly = true)
    public FixedDeposit getFixedDepositById(Long userId, Long fdId) {

        FixedDeposit fd = fixedDepositRepository
                .findByIdAndUserId(fdId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed Deposit not found for user"));

        enrichWithAccruedInterest(fd);
        return fd;
    }

    // Update Fixed Deposit Status of a particular FD of a user
    @Transactional
    public FixedDeposit updateFixedDepositStatus(
            Long fdId,
            FDStatus newStatus
    ) {
        FixedDeposit fd = fixedDepositRepository
                .findById(fdId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed Deposit not found for user"));

        FDStatus currentStatus = fd.getStatus();

        // ACTIVE -> MATURED
        if (currentStatus == FDStatus.ACTIVE && newStatus == FDStatus.MATURED) {
            if (LocalDate.now().isBefore(fd.getMaturityDate())) {
                throw new BusinessException("FD has not yet reached maturity date");
            }
            fd.setStatus(FDStatus.MATURED);
            return fixedDepositRepository.save(fd);
        }

        // ACTIVE -> BROKEN
        if (currentStatus == FDStatus.ACTIVE && newStatus == FDStatus.BROKEN) {
            if (!fd.getInterestScheme().isPrematureBreakAllowed()) {
                throw new BusinessException("Premature withdrawal is not allowed for this FD scheme");
            }
            if (!LocalDate.now().isBefore(fd.getMaturityDate())) {
                throw new BusinessException("FD has already matured; use withdrawal instead");
            }
            fd.setStatus(FDStatus.BROKEN);
            return fixedDepositRepository.save(fd);
        }

        // MATURED -> CLOSED
        if (currentStatus == FDStatus.MATURED && newStatus == FDStatus.CLOSED) {
            if (LocalDate.now().isBefore(fd.getMaturityDate())) {
                throw new BusinessException("FD cannot be closed before maturity date");
            }
            fd.setStatus(FDStatus.CLOSED);
            return fixedDepositRepository.save(fd);
        }

        // INVALID Transition
        throw new BusinessException(String.format("Invalid FD status transition from %s to %s", currentStatus, newStatus));
    }


    // Helper methods

    private void enrichWithAccruedInterest(FixedDeposit fd) {

        if (fd.getStatus() == FDStatus.ACTIVE || fd.getStatus() == FDStatus.MATURED) {

            fd.setAccruedInterest(interestCalculationService.calculateAccruedInterest(fd));
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(MIN_FD_AMOUNT) < 0) {
            throw new BusinessException("Minimum Fixed Deposit amount is " + MIN_FD_AMOUNT);
        }
    }
}
