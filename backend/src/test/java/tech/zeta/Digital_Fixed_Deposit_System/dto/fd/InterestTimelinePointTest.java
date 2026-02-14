package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

// Author - Arpit Chaurasia
class InterestTimelinePointTest {

    @Test
    void constructor_setsFields() {
        InterestTimelinePoint point = new InterestTimelinePoint("M1", new BigDecimal("12.34"));
        assertThat(point.getPeriod()).isEqualTo("M1");
        assertThat(point.getAccruedInterest()).isEqualByComparingTo("12.34");
    }
}

