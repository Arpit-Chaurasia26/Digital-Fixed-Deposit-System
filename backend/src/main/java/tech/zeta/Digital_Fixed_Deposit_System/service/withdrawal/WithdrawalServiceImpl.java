package tech.zeta.Digital_Fixed_Deposit_System.service.withdrawal;

import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.constants.FDConstants;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalEligibility;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalHistory;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalPreview;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalReceipt;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.transaction.Transaction;
import tech.zeta.Digital_Fixed_Deposit_System.exception.AccountNotFoundException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.InSufficientFundsException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.InvalidOperationException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;
import tech.zeta.Digital_Fixed_Deposit_System.repository.TransactionRepository;
import tech.zeta.Digital_Fixed_Deposit_System.util.InterestUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
public class WithdrawalServiceImpl implements WithdrawalService{


    private FixedDepositRepository fixedDepositRepository;
    private CurrentUserProvider currentUserProvider;
    private TransactionRepository transactionRepository;

    public WithdrawalServiceImpl(FixedDepositRepository fixedDepositRepository, CurrentUserProvider currentUserProvider, TransactionRepository transactionRepository){
        this.fixedDepositRepository = fixedDepositRepository;
        this.currentUserProvider = currentUserProvider;
        this.transactionRepository = transactionRepository;
    }

    private FixedDeposit validateFixedDeposit(Long id){
        FixedDeposit fixedDeposit = fixedDepositRepository.findById(id)
                .orElseThrow(()-> {
                    log.error("No Fixed Deposit Found with ID {}", id);
                    return new AccountNotFoundException("FD account not found");
                });

        Long userId = currentUserProvider.getCurrentUserId();
        if(!Objects.equals(fixedDeposit.getUserId(), userId)){
            log.error("User with userId: {} can not access fixed deposit with id: {}", userId, id);
            throw new UnauthorizedException("Unauthorized Access");
        }

        return fixedDeposit;
    }

    @Override
    public WithdrawalPreview getWithdrawalPreview(Long id, BigDecimal withdrawalAmount) {

        log.info("Processing withdrawal preview for Fixed Deposit with ID: {} of amount {}", id, withdrawalAmount);

        if(withdrawalAmount.compareTo(BigDecimal.ZERO)<=0)throw new ValidationException("Amount should be positive");

        FixedDeposit fixedDeposit = validateFixedDeposit(id);

        if(fixedDeposit.getStatus()!=FDStatus.ACTIVE && fixedDeposit.getStatus()!=FDStatus.MATURED ){
            log.error("Request failed due to accessing closed/broken fixed deposit with Id: {}", id);
            throw new InvalidOperationException("Fixed Deposit has been CLOSED");
        }

        WithdrawalPreview preview;

        BigDecimal interestAccrued;
        BigDecimal principleAmount = fixedDeposit.getAmount();
        if(withdrawalAmount.compareTo(principleAmount)>0){
            log.error("Attempted to withdraw {} for fixed deposit Id: {}",withdrawalAmount, id);
            throw new InSufficientFundsException("Can not withdraw more amount greater than the principal amount");
        }
        BigDecimal actualInterestRate = fixedDeposit.getInterestRate();
        BigDecimal interestWithPenalty = actualInterestRate.subtract(FDConstants.PENALTY);

        //Even the FD persists after Maturity the maximum number of months are considered only Tenure
        Long numberOfMonths = Long.min(ChronoUnit.MONTHS.between(fixedDeposit.getStartDate(), LocalDate.now()), fixedDeposit.getTenureMonths());

        //Matured FD
        if(fixedDeposit.getMaturityDate().compareTo(LocalDate.now())<=0){
            interestAccrued = InterestUtils.calculateInterest(withdrawalAmount, actualInterestRate, numberOfMonths);
            preview = new WithdrawalPreview(withdrawalAmount, interestAccrued,actualInterestRate, BigDecimal.ZERO, interestAccrued, principleAmount.subtract(withdrawalAmount));
        }
        //Breaking the FD before Maturity
        else if(fixedDeposit.getInterestScheme().isPrematureBreakAllowed()) {
            BigDecimal netInterest;

            //Broken within three months No INTEREST
            if(numberOfMonths<3){
                interestAccrued = BigDecimal.ZERO;
                netInterest=new BigDecimal(0);
                preview = new WithdrawalPreview(withdrawalAmount, interestAccrued,actualInterestRate, BigDecimal.ZERO, netInterest, principleAmount.subtract(withdrawalAmount));
            }
            //Broken After Three Months Apply PENALTY
            else {
                interestAccrued = InterestUtils.calculateInterest(withdrawalAmount, actualInterestRate, numberOfMonths);
                netInterest = InterestUtils.calculateInterest(withdrawalAmount, interestWithPenalty, numberOfMonths);//After removing the penalty
                preview = new WithdrawalPreview(withdrawalAmount, interestAccrued, actualInterestRate, FDConstants.PENALTY, netInterest, principleAmount.subtract(withdrawalAmount));
            }
        }
        //Breaking FD Not Allowed
        else{
            log.error("Request failed due to break is not applicable on fixed deposit Id: {}", id);
            throw new InvalidOperationException("You can not break a this Fixed Deposit");
        }
        log.info("Withdrawal preview generated successfully for FD ID: {}", id);
        return preview;

    }

    @Override
    @Transactional
    public WithdrawalReceipt confirmWithdrawal(Long id, BigDecimal withdrawalAmount) {

        log.info("Processing for confirming withdrawal of fixed deposit Id: {}", id);
        FixedDeposit fixedDeposit = validateFixedDeposit(id);
        if(fixedDeposit.getStatus()!=FDStatus.ACTIVE && fixedDeposit.getStatus()!=FDStatus.MATURED ){
            log.error("Request failed due to accessing closed/broken fixed deposit Id: {}", id);
            throw new InvalidOperationException("Fixed Deposit has been CLOSED");
        }

        WithdrawalPreview withdrawalPreview = getWithdrawalPreview(id, withdrawalAmount);
        WithdrawalReceipt withdrawalReceipt = new WithdrawalReceipt(
               id,
                withdrawalPreview.getWithdrawalAmount(),
                withdrawalPreview.getNetInterestAmount(),
                withdrawalPreview.getInterestRate().subtract(withdrawalPreview.getPenalty()),
                fixedDeposit.getStartDate(),
                fixedDeposit.getMaturityDate(),
                LocalDate.now()
        );
        BigDecimal principleAmount = fixedDeposit.getAmount();

        if(withdrawalAmount.compareTo(principleAmount)<0){
            fixedDeposit.setAmount(principleAmount.subtract(withdrawalAmount));
            fixedDeposit.setAccruedInterest(calculateInterest(fixedDeposit.getAmount(), fixedDeposit.getInterestRate(), ChronoUnit.MONTHS.between(fixedDeposit.getStartDate(), LocalDate.now())));
        }else{

            fixedDeposit.setAccruedInterest(withdrawalPreview.getNetInterestAmount());

            if(fixedDeposit.getMaturityDate().isAfter(LocalDate.now())){
                fixedDeposit.setStatus(FDStatus.BROKEN);
            }else{
                fixedDeposit.setStatus(FDStatus.CLOSED);
            }
        }
        Transaction transaction = new Transaction();
        transaction.setFdId(id);
        transaction.setAmountPaid(withdrawalAmount);
        transaction.setInterestPaid(withdrawalReceipt.getAccruedInterest());
        transaction.setUserId(currentUserProvider.getCurrentUserId());
        transactionRepository.save(transaction);
        fixedDepositRepository.save(fixedDeposit);
        log.info("Successfully withdrawn amount of {} from the fixed deposit Id: {} to {}",withdrawalAmount, id, fixedDeposit.getStatus());
        return withdrawalReceipt;

    }

    //Checking Withdrawal Eligibility
    public WithdrawalEligibility checkWithdrawalEligibility(Long id){
        log.info("Processing the eligibility check for fixed deposit Id: {}", id);
        FixedDeposit fixedDeposit = validateFixedDeposit(id);

        WithdrawalEligibility withdrawalEligibility;

        LocalDate today = LocalDate.now();
        LocalDate maturityDate = fixedDeposit.getMaturityDate();
        if(fixedDeposit.getStatus()!=FDStatus.ACTIVE && fixedDeposit.getStatus()!=FDStatus.MATURED ){
            withdrawalEligibility = new WithdrawalEligibility(false, "Fixed Deposit is Already Closed");
        }
        else if(maturityDate.compareTo(today)<=0){
            withdrawalEligibility = new WithdrawalEligibility(true, "Fixed Deposit has matured");
        } else if (fixedDeposit.getInterestScheme().isPrematureBreakAllowed()) {
            withdrawalEligibility = new WithdrawalEligibility(true, "WARNING! Withdrawing causes penalty");
        }else{
            withdrawalEligibility = new WithdrawalEligibility(false, "You can not Withdraw");
        }
        log.info("Eligibility Checked successfully for fixed deposit Id: {}", id);
        return withdrawalEligibility;
    }

    @Override
    public List<WithdrawalHistory> getWithdrawalHistory(Long userId) {
        Long loggedInUserId = currentUserProvider.getCurrentUserId();
        if(!loggedInUserId.equals(userId)){
            throw new UnauthorizedException("Unauthorized Access to withdrawal history");
        }
        List<Transaction> listOfTransactions = transactionRepository.findByUserId(userId);
        List<WithdrawalHistory> listOfWithdrawalHistory = listOfTransactions.stream()
                .map(transaction->new WithdrawalHistory(transaction.getFdId(), transaction.getAmountPaid(), transaction.getInterestPaid(), transaction.getCreatedAt().toLocalDate()))
                .toList();
        System.out.println(listOfWithdrawalHistory);
        return listOfWithdrawalHistory;

    }
}
