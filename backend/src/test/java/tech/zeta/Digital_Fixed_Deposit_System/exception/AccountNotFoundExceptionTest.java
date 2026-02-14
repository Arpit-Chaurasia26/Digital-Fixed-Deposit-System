package tech.zeta.Digital_Fixed_Deposit_System.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
Author : Priyanshu Mishra
*/


@DisplayName("AccountNotFoundException Tests")
class AccountNotFoundExceptionTest {

    @Test
    void message_isStored() {
        AccountNotFoundException ex = new AccountNotFoundException("missing");
        assertThat(ex.getMessage()).isEqualTo("missing");
    }
}

