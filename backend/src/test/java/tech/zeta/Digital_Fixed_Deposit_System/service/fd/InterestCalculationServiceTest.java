package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterestCalculationServiceTest {

    @Test
    void calculateAccruedInterest_monthlyScheme() {
        InterestCalculationService service = new InterestCalculationService();

        FixedDeposit fd = new FixedDeposit();
        fd.setStatus(FDStatus.ACTIVE);
        fd.setAmount(new BigDecimal("1000"));
        fd.setInterestScheme(InterestScheme.STANDARD_6_MONTHS);
        fd.setInterestRate(new BigDecimal("5.50"));
        fd.setStartDate(LocalDate.now().minusDays(30));
        fd.setMaturityDate(LocalDate.now());

        BigDecimal interest = service.calculateAccruedInterest(fd);

        assertEquals(0, interest.compareTo(new BigDecimal("4.58")));
    }

    @Test
    void calculateAccruedInterest_returnsZeroForNonAccruingStatus() {
        InterestCalculationService service = new InterestCalculationService();

        FixedDeposit fd = new FixedDeposit();
        fd.setStatus(FDStatus.BROKEN);
        fd.setAmount(new BigDecimal("1000"));
        fd.setInterestScheme(InterestScheme.STANDARD_6_MONTHS);
        fd.setInterestRate(new BigDecimal("5.50"));
        fd.setStartDate(LocalDate.now().minusDays(10));
        fd.setMaturityDate(LocalDate.now().plusDays(10));

        BigDecimal interest = service.calculateAccruedInterest(fd);

        assertEquals(0, interest.compareTo(BigDecimal.ZERO));
    }
}
