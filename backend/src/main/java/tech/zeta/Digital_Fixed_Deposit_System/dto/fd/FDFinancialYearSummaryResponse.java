package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import java.math.BigDecimal;

/**
 * @author Arpit Chaurasia
 */
public class FDFinancialYearSummaryResponse {

    private String financialYear;
    private long totalFDsCreated;
    private BigDecimal totalPrincipalDeposited;
    private BigDecimal totalInterestAccruedTillDate;

    public FDFinancialYearSummaryResponse(
            String financialYear,
            long totalFDsCreated,
            BigDecimal totalPrincipalDeposited,
            BigDecimal totalInterestAccruedTillDate
    ) {
        this.financialYear = financialYear;
        this.totalFDsCreated = totalFDsCreated;
        this.totalPrincipalDeposited = totalPrincipalDeposited;
        this.totalInterestAccruedTillDate = totalInterestAccruedTillDate;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public long getTotalFDsCreated() {
        return totalFDsCreated;
    }

    public BigDecimal getTotalPrincipalDeposited() {
        return totalPrincipalDeposited;
    }

    public BigDecimal getTotalInterestAccruedTillDate() {
        return totalInterestAccruedTillDate;
    }
}
