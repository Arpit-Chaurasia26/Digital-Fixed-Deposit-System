package tech.zeta.Digital_Fixed_Deposit_System.service.fd.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

// Author - Arpit Chaurasia
@DisplayName("FDStatusTransitionHandler Interface Tests")
class FDStatusTransitionHandlerTest {

    @Test
    void interface_contract_isStable() {
        assertThat(FDStatusTransitionHandler.class.isInterface()).isTrue();
        Method[] methods = FDStatusTransitionHandler.class.getDeclaredMethods();
        assertThat(methods.length).isEqualTo(2);
    }
}

