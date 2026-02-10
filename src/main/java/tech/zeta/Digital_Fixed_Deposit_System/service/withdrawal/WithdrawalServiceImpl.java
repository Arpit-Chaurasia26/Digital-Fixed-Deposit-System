package tech.zeta.Digital_Fixed_Deposit_System.service.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.zeta.Digital_Fixed_Deposit_System.constants.FDConstants;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalPreview;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalReceipt;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;
import tech.zeta.Digital_Fixed_Deposit_System.exception.AccountNotFoundException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.InvalidOperationException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;
import tech.zeta.Digital_Fixed_Deposit_System.util.InterestUtils;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class WithdrawalServiceImpl implements WithdrawalService{

    private FixedDepositRepository fixedDepositRepository;

    @Autowired
    private WithdrawalServiceImpl(FixedDepositRepository fixedDepositRepository){
        this.fixedDepositRepository = fixedDepositRepository;
    }


    @Override
    public WithdrawalPreview getWithdrawalPreview(int id) {
        System.out.println("I have been Called");
        FixedDeposit fixedDeposit = fixedDepositRepository.findById(id)
              .orElseThrow(()->new AccountNotFoundException("FD account not found"));
        System.out.println("Exited");
        //Need to authenticate the User

        WithdrawalPreview preview;

        //Monthly Interest
        if(fixedDeposit.getInterestScheme().getInterestFrequency()== InterestFrequency.MONTHLY){

            BigDecimal interestAccrued;
            BigDecimal principleAmount = BigDecimal.valueOf(20000);
            BigDecimal actualInterestRate = fixedDeposit.getInterestRate();
            BigDecimal interestWithPenalty = actualInterestRate.subtract(FDConstants.PENALTY);
            Long numberOfMonths = ChronoUnit.MONTHS.between(fixedDeposit.getStartDate(), LocalDate.now());

            //Pre-matured Monthly Interest
            if(fixedDeposit.getMaturityDate().isAfter(LocalDate.now())){
                BigDecimal netInterest;

                //Broken within three months
                if(numberOfMonths<=3){
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
            //Matured Monthly Interest
            else {
                interestAccrued = InterestUtils.calculateInterest(principleAmount, actualInterestRate, numberOfMonths);
                preview = new WithdrawalPreview(principleAmount, interestAccrued,actualInterestRate, BigDecimal.ZERO, interestAccrued);
            }
        }
        //Yearly Interest
        else{
            BigDecimal interestAccrued;
            BigDecimal principleAmount = fixedDeposit.getAmount();
            BigDecimal actualInterestRate = fixedDeposit.getInterestRate();

            //Pre-matured Yearly Interest
            if(fixedDeposit.getMaturityDate().isAfter(LocalDate.now())){
                throw new InvalidOperationException("You can not break a Yearly Fixed Deposit");
            }
            //Matured Yearly Interest
            else {
                interestAccrued = InterestUtils.calculateInterest(principleAmount, fixedDeposit.getInterestRate(), Long.valueOf(fixedDeposit.getTenureMonths()));
                preview = new WithdrawalPreview(principleAmount, interestAccrued,actualInterestRate, new BigDecimal(0), interestAccrued);
            }
        }
        return preview;

    }

    @Override
    public WithdrawalReceipt confirmWithdrawal(int id) {

        FixedDeposit fixedDeposit = fixedDepositRepository.findById(id)
                .orElseThrow(()->new AccountNotFoundException("FD account not found"));

        //Need to authenticate the User

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
}
