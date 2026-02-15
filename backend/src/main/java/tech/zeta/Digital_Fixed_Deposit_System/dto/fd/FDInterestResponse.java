package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import lombok.Builder;
import lombok.Getter;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Arpit Chaurasia
 */
@Builder
@Getter
public class FDInterestResponse {

    private Long fdId;
    private FDStatus status;
    private BigDecimal amount;
    private BigDecimal accruedInterest;
    private BigDecimal interestRate;
    private InterestFrequency interestFrequency;
    private LocalDate startDate;
    private LocalDate maturityDate;

}
