package tech.zeta.Digital_Fixed_Deposit_System.service.fd.interest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

// Author - Arpit Chaurasia
@DisplayName("InterestCalculationStrategy Interface Tests")
class InterestCalculationStrategyTest {

    @Test
    void interface_contract_isStable() {
        assertThat(InterestCalculationStrategy.class.isInterface()).isTrue();
        Method[] methods = InterestCalculationStrategy.class.getDeclaredMethods();
        assertThat(methods.length).isEqualTo(2);
    }
}

