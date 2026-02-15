package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Arpit Chaurasia
 */
public class FDPortfolioResponse {

    private long totalFDs;
    private long activeFDs;
    private long maturedFDs;
    private long brokenFDs;

    private BigDecimal totalPrincipal;
    private BigDecimal totalInterestAccrued;

    private LocalDate nextMaturityDate;

    public FDPortfolioResponse(
            long totalFDs,
            long activeFDs,
            long maturedFDs,
            long brokenFDs,
            BigDecimal totalPrincipal,
            BigDecimal totalInterestAccrued,
            LocalDate nextMaturityDate
    ) {
        this.totalFDs = totalFDs;
        this.activeFDs = activeFDs;
        this.maturedFDs = maturedFDs;
        this.brokenFDs = brokenFDs;
        this.totalPrincipal = totalPrincipal;
        this.totalInterestAccrued = totalInterestAccrued;
        this.nextMaturityDate = nextMaturityDate;
    }

    public long getTotalFDs() {
        return totalFDs;
    }

    public long getActiveFDs() {
        return activeFDs;
    }

    public long getMaturedFDs() {
        return maturedFDs;
    }

    public long getBrokenFDs() {
        return brokenFDs;
    }

    public BigDecimal getTotalPrincipal() {
        return totalPrincipal;
    }

    public BigDecimal getTotalInterestAccrued() {
        return totalInterestAccrued;
    }

    public LocalDate getNextMaturityDate() {
        return nextMaturityDate;
    }
}
