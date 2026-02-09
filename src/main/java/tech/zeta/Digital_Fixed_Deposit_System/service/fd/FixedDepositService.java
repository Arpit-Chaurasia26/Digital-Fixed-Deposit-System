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


    public FixedDepositService(
            FixedDepositRepository fixedDepositRepository
            ) {
        this.fixedDepositRepository = fixedDepositRepository;
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


    @Transactional(readOnly = true)
    public List<FixedDeposit> getFixedDepositsByUser(Long userId) {


        List<FixedDeposit> fds = fixedDepositRepository.findByUserId(userId);


        return fds;
    }


    // Fetch a specific Fixed Deposit of a user.
    @Transactional(readOnly = true)
    public FixedDeposit getFixedDepositById(Long userId, Long fdId) {


        FixedDeposit fd = fixedDepositRepository
                .findByIdAndUserId(fdId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed Deposit not found for user"));


        return fd;
    }



    // Helper methods


    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(MIN_FD_AMOUNT) < 0) {
            throw new BusinessException("Minimum Fixed Deposit amount is " + MIN_FD_AMOUNT);
        }
    }
}

