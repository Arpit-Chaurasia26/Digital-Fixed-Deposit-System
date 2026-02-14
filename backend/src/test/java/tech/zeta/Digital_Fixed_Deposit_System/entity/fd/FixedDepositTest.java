package tech.zeta.Digital_Fixed_Deposit_System.entity.fd;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// Author - Arpit Chaurasia
public class FixedDepositTest {

    @Test
    void onCreate_setsDefaults() throws Exception {
        FixedDeposit fd = new FixedDeposit();
        invokeLifecycle(fd, "onCreate");

        assertNotNull(fd.getCreatedAt());
        assertEquals(FDStatus.ACTIVE, fd.getStatus());
        assertEquals(BigDecimal.ZERO, fd.getAccruedInterest());
    }

    @Test
    void onUpdate_setsUpdatedAt() throws Exception {
        FixedDeposit fd = new FixedDeposit();
        invokeLifecycle(fd, "onUpdate");

        assertNotNull(fd.getUpdatedAt());
    }

    @Test
    void gettersAndSetters_workAsExpected() {
        FixedDeposit fd = new FixedDeposit();
        fd.setUserId(99L);
        fd.setAmount(new BigDecimal("8000"));
        fd.setInterestScheme(InterestScheme.STANDARD_6_MONTHS);
        fd.setInterestRate(new BigDecimal("5.50"));
        fd.setTenureMonths(6);
        fd.setStartDate(LocalDate.now().minusDays(10));
        fd.setMaturityDate(LocalDate.now().plusDays(10));
        fd.setStatus(FDStatus.ACTIVE);
        fd.setAccruedInterest(new BigDecimal("45.67"));

        assertEquals(99L, fd.getUserId());
        assertEquals(new BigDecimal("8000"), fd.getAmount());
        assertEquals(InterestScheme.STANDARD_6_MONTHS, fd.getInterestScheme());
        assertEquals(new BigDecimal("5.50"), fd.getInterestRate());
        assertEquals(6, fd.getTenureMonths());
        assertEquals(FDStatus.ACTIVE, fd.getStatus());
        assertEquals(new BigDecimal("45.67"), fd.getAccruedInterest());
    }

    private void invokeLifecycle(FixedDeposit fd, String method) throws Exception {
        Method lifecycle = FixedDeposit.class.getDeclaredMethod(method);
        lifecycle.setAccessible(true);
        lifecycle.invoke(fd);
    }
}
