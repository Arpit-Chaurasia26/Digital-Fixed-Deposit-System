package tech.zeta.Digital_Fixed_Deposit_System.entity.fd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// Author - Arpit Chaurasia
@DisplayName("InterestFrequency Enum Tests")
class InterestFrequencyTest {

    @Test
    void values_areStable() {
        assertThat(InterestFrequency.values()).containsExactly(
                InterestFrequency.MONTHLY,
                InterestFrequency.YEARLY
        );
    }
}

