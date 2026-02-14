package tech.zeta.Digital_Fixed_Deposit_System.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/*
Author : Priyanshu Mishra
*/


@DisplayName("IUserService Interface Tests")
class IUserServiceTest {

    @Test
    void interface_contract_isStable() {
        assertThat(IUserService.class.isInterface()).isTrue();
        Method[] methods = IUserService.class.getDeclaredMethods();
        assertThat(methods.length).isEqualTo(3);
    }
}

