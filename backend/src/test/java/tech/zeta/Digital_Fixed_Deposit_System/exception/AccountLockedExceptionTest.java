package tech.zeta.Digital_Fixed_Deposit_System.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccountLockedException Tests")
class AccountLockedExceptionTest {

    @Test
    void message_andRemainingSeconds_areStored() {
        AccountLockedException ex = new AccountLockedException("locked", 45L);
        assertThat(ex.getMessage()).isEqualTo("locked");
        assertThat(ex.getRemainingSeconds()).isEqualTo(45L);
    }
}

