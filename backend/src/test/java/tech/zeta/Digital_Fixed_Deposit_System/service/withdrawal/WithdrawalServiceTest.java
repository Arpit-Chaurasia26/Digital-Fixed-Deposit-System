package tech.zeta.Digital_Fixed_Deposit_System.service.withdrawal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WithdrawalService Interface Tests")
class WithdrawalServiceTest {

    @Test
    void interface_contract_isStable() {
        assertThat(WithdrawalService.class.isInterface()).isTrue();
        Method[] methods = WithdrawalService.class.getDeclaredMethods();
        assertThat(methods.length).isEqualTo(4);
    }
}

