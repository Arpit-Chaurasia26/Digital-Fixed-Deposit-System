package tech.zeta.Digital_Fixed_Deposit_System.service.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.constants.FDConstants;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalEligibility;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalPreview;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalReceipt;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;
import tech.zeta.Digital_Fixed_Deposit_System.exception.AccountNotFoundException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.InvalidOperationException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;
import tech.zeta.Digital_Fixed_Deposit_System.util.InterestUtils;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
public class WithdrawalServiceImpl implements WithdrawalService{

    private FixedDepositRepository fixedDepositRepository;
    private CurrentUserProvider currentUserProvider;

    public WithdrawalServiceImpl(FixedDepositRepository fixedDepositRepository, CurrentUserProvider currentUserProvider){
        this.fixedDepositRepository = fixedDepositRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public WithdrawalPreview getWithdrawalPreview(Long id) {

        FixedDeposit fixedDeposit = fixedDepositRepository.findById(id)
              .orElseThrow(()->new AccountNotFoundException("FD account not found"));
        //Need to authenticate the User
        Long userId = currentUserProvider.getCurrentUserId();
        if(!Objects.equals(fixedDeposit.getUserId(), userId)){
            throw new UnauthorizedException("Unauthorized Access");
        }


        WithdrawalPreview preview=null;

        BigDecimal interestAccrued;
        BigDecimal principleAmount = BigDecimal.valueOf(20000);
        BigDecimal actualInterestRate = fixedDeposit.getInterestRate();
        BigDecimal interestWithPenalty = actualInterestRate.subtract(FDConstants.PENALTY);
        Long numberOfMonths = ChronoUnit.MONTHS.between(fixedDeposit.getStartDate(), LocalDate.now());

        //Matured FD
        if(fixedDeposit.getMaturityDate().compareTo(LocalDate.now())<=0){
            interestAccrued = InterestUtils.calculateInterest(principleAmount, actualInterestRate, numberOfMonths);
            preview = new WithdrawalPreview(principleAmount, interestAccrued,actualInterestRate, BigDecimal.ZERO, interestAccrued);
        }
        //Breaking the FD before Maturity
        else if(fixedDeposit.getInterestScheme().isPrematureBreakAllowed()) {
            BigDecimal netInterest;

            //Broken within three months
            if(numberOfMonths<3){
                interestAccrued = BigDecimal.ZERO;
                netInterest=new BigDecimal(0);
                preview = new WithdrawalPreview(principleAmount, interestAccrued,actualInterestRate, BigDecimal.ZERO, netInterest);
            }
            //Broken After Three Months
            else {
                interestAccrued = InterestUtils.calculateInterest(principleAmount, actualInterestRate, numberOfMonths);
                netInterest = InterestUtils.calculateInterest(principleAmount, interestWithPenalty, numberOfMonths);
                preview = new WithdrawalPreview(principleAmount, interestAccrued, actualInterestRate, FDConstants.PENALTY, netInterest);
            }
        }
        //Breaking FD Not Allowed
        else{
            throw new InvalidOperationException("You can not break a Yearly Fixed Deposit");
        }


        return preview;

    }

    @Override
    public WithdrawalReceipt confirmWithdrawal(Long id) {

        FixedDeposit fixedDeposit = fixedDepositRepository.findById(id)
                .orElseThrow(()->new AccountNotFoundException("FD account not found"));

        //Need to authenticate the User
        Long userId = currentUserProvider.getCurrentUserId();
        if(!Objects.equals(fixedDeposit.getUserId(), userId)){
            throw new UnauthorizedException("Unauthorized Access");
        }

        WithdrawalPreview withdrawalPreview = getWithdrawalPreview(id);
        WithdrawalReceipt withdrawalReceipt = new WithdrawalReceipt(
               id,
                withdrawalPreview.getPrincipleAmount(),
                withdrawalPreview.getNetInterestAmount(),
                withdrawalPreview.getInterestRate(),
                fixedDeposit.getStartDate(),
                fixedDeposit.getMaturityDate(),
                LocalDate.now()
        );
        fixedDeposit.setAccruedInterest(withdrawalPreview.getNetInterestAmount());
        if(fixedDeposit.getMaturityDate().isAfter(LocalDate.now())){
            fixedDeposit.setStatus(FDStatus.BROKEN);
        }

        //fixedDeposit.setUpdatedAt

        fixedDepositRepository.save(fixedDeposit);

        return withdrawalReceipt;

    }

    //Checking Withdrawal Eligibility
    public WithdrawalEligibility checkWithdrawalEligibility(Long id){
        FixedDeposit fixedDeposit = fixedDepositRepository.findById(id)
                .orElseThrow(()->new AccountNotFoundException("FD account not found"));

        //Authenticate User
        Long userId = currentUserProvider.getCurrentUserId();
        if(!Objects.equals(fixedDeposit.getUserId(), userId)){
            throw new UnauthorizedException("Unauthorized Access");
        }

        WithdrawalEligibility withdrawalEligibility;

        LocalDate today = LocalDate.now();
        LocalDate maturityDate = fixedDeposit.getMaturityDate();
        if(maturityDate.compareTo(today)<=0){
            withdrawalEligibility = new WithdrawalEligibility(true, "FixedD has matured");
        } else if (fixedDeposit.getInterestScheme().isPrematureBreakAllowed()) {
            withdrawalEligibility = new WithdrawalEligibility(true, "WARNING! Withdrawing causes penalty");
        }else{
            withdrawalEligibility = new WithdrawalEligibility(false, "You can not Withdraw");
        }
        return withdrawalEligibility;
    }
}
