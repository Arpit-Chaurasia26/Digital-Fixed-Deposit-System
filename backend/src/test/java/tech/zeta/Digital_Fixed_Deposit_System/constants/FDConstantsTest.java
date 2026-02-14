package tech.zeta.Digital_Fixed_Deposit_System.constants;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FDConstantsTest {

    @Test
    void penalty_isOne() {
        assertThat(FDConstants.PENALTY).isEqualByComparingTo(BigDecimal.ONE);
    }
}

