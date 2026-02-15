package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Arpit Chaurasia
 */
public class FixedDepositMaturitySchedulerTest {

    @Test
    void markMaturedFDs_updatesEligibleOnes() {
        List<FixedDeposit> store = new ArrayList<>();
        FixedDeposit matured = new FixedDeposit();
        matured.setStatus(FDStatus.ACTIVE);
        matured.setMaturityDate(LocalDate.now().minusDays(1));

        FixedDeposit notMatured = new FixedDeposit();
        notMatured.setStatus(FDStatus.ACTIVE);
        notMatured.setMaturityDate(LocalDate.now().plusDays(2));

        store.add(matured);
        store.add(notMatured);

        FixedDepositRepository repository = (FixedDepositRepository) Proxy.newProxyInstance(
                FixedDepositRepository.class.getClassLoader(),
                new Class<?>[]{FixedDepositRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("findByStatus")) {
                        return store;
                    }
                    if (method.getName().equals("saveAll")) {
                        @SuppressWarnings("unchecked")
                        List<FixedDeposit> saved = (List<FixedDeposit>) args[0];
                        return saved;
                    }
                    return null;
                }
        );

        FixedDepositMaturityScheduler scheduler = new FixedDepositMaturityScheduler(repository);
        scheduler.markMaturedFDs();

        assertEquals(FDStatus.MATURED, store.get(0).getStatus());
        assertEquals(FDStatus.ACTIVE, store.get(1).getStatus());
    }
}
