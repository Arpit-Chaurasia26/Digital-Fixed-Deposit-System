package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

// Author - Arpit Chaurasia
public class FDMaturityResponse {

    private Long fdId;
    private Long userId;
    private BigDecimal amount;
    private LocalDate maturityDate;
    private long daysRemaining;
    private FDStatus status;

    public FDMaturityResponse(
            Long fdId,
            Long userId,
            BigDecimal amount,
            LocalDate maturityDate,
            long daysRemaining,
            FDStatus status
    ) {
        this.fdId = fdId;
        this.userId = userId;
        this.amount = amount;
        this.maturityDate = maturityDate;
        this.daysRemaining = daysRemaining;
        this.status = status;
    }

    public Long getFdId() { return fdId; }
    public Long getUserId() { return userId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getMaturityDate() { return maturityDate; }
    public long getDaysRemaining() { return daysRemaining; }
    public FDStatus getStatus() { return status; }
}
