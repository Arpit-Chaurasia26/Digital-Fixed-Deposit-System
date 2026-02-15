package tech.zeta.Digital_Fixed_Deposit_System.entity.fd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arpit Chaurasia
 */
@DisplayName("InterestScheme Enum Tests")
class InterestSchemeTest {

    @Test
    void scheme_properties_areConsistent() {
        InterestScheme scheme = InterestScheme.STANDARD_6_MONTHS;

        assertThat(scheme.getAnnualInterestRate().toPlainString()).isEqualTo("5.50");
        assertThat(scheme.getTenureInMonths()).isEqualTo(6);
        assertThat(scheme.getInterestFrequency()).isEqualTo(InterestFrequency.MONTHLY);
        assertThat(scheme.isPrematureBreakAllowed()).isTrue();
    }
}

