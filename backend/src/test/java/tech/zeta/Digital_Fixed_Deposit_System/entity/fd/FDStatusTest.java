package tech.zeta.Digital_Fixed_Deposit_System.entity.fd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// Author - Arpit Chaurasia
@DisplayName("FDStatus Enum Tests")
class FDStatusTest {

    @Test
    void values_areStable() {
        assertThat(FDStatus.values()).containsExactly(
                FDStatus.ACTIVE,
                FDStatus.MATURED,
                FDStatus.CLOSED,
                FDStatus.BROKEN
        );
    }
}

