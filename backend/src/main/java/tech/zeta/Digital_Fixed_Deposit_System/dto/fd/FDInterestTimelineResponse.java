package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Arpit Chaurasia
 */
@Builder
@Getter
public class FDInterestTimelineResponse {

    private Long fdId;
    private BigDecimal principal;
    private BigDecimal interestRate;
    private String interval;
    private List<InterestTimelinePoint> timeline;

}
