package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arpit Chaurasia
 */
class FDInterestTimelineResponseTest {

    @Test
    void constructor_setsFields() {
        InterestTimelinePoint point = new InterestTimelinePoint("M1", new BigDecimal("10.00"));
        FDInterestTimelineResponse response = new FDInterestTimelineResponse(
                1L,
                new BigDecimal("1000.00"),
                new BigDecimal("6.50"),
                "MONTHLY",
                List.of(point)
        );

        assertThat(response.getFdId()).isEqualTo(1L);
        assertThat(response.getPrincipal()).isEqualByComparingTo("1000.00");
        assertThat(response.getInterestRate()).isEqualByComparingTo("6.50");
        assertThat(response.getInterval()).isEqualTo("MONTHLY");
        assertThat(response.getTimeline()).hasSize(1);
    }
}

