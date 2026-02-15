package tech.zeta.Digital_Fixed_Deposit_System.entity.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Transaction Entity Tests")
class TransactionTest {

    @Test
    void setters_getters_andPrePersist_work() throws Exception {
        Transaction tx = new Transaction();
        tx.setFdId(10L);
        tx.setUserId(20L);
        tx.setAmountPaid(new BigDecimal("1000.00"));
        tx.setInterestPaid(new BigDecimal("50.00"));

        Method onCreate = Transaction.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(tx);

        assertThat(tx.getFdId()).isEqualTo(10L);
        assertThat(tx.getUserId()).isEqualTo(20L);
        assertThat(tx.getAmountPaid()).isEqualByComparingTo("1000.00");
        assertThat(tx.getInterestPaid()).isEqualByComparingTo("50.00");
        assertThat(tx.getCreatedAt()).isNotNull();
    }
}

