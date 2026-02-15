package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Arpit Chaurasia
 */
@Builder
@Getter
public class FDPortfolioResponse {

    private long totalFDs;
    private long activeFDs;
    private long maturedFDs;
    private long brokenFDs;
    private BigDecimal totalPrincipal;
    private BigDecimal totalInterestAccrued;
    private LocalDate nextMaturityDate;

}
