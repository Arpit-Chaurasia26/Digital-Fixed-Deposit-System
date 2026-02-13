package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import java.math.BigDecimal;
import java.util.List;

// Author - Arpit Chaurasia
public class FDInterestTimelineResponse {

    private Long fdId;
    private BigDecimal principal;
    private BigDecimal interestRate;
    private String interval;
    private List<InterestTimelinePoint> timeline;

    public FDInterestTimelineResponse(
            Long fdId,
            BigDecimal principal,
            BigDecimal interestRate,
            String interval,
            List<InterestTimelinePoint> timeline
    ) {
        this.fdId = fdId;
        this.principal = principal;
        this.interestRate = interestRate;
        this.interval = interval;
        this.timeline = timeline;
    }

    public Long getFdId() { return fdId; }
    public BigDecimal getPrincipal() { return principal; }
    public BigDecimal getInterestRate() { return interestRate; }
    public String getInterval() { return interval; }
    public List<InterestTimelinePoint> getTimeline() { return timeline; }
}
