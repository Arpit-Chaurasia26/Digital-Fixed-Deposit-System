package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.BookFDRequest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.ResourceNotFoundException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.interest.MonthlySimpleInterestStrategy;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.interest.YearlySimpleInterestStrategy;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.status.ActiveToBrokenTransitionHandler;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.status.ActiveToMaturedTransitionHandler;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.status.MaturedToClosedTransitionHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FixedDepositServiceTest {

    @Test
    void bookFixedDeposit_throwsWhenBelowMinimum() throws Exception {
        FixedDepositRepository repository = proxyRepository(new FixedDeposit());
        InterestCalculationService interestCalculationService = new InterestCalculationService(
                List.of(new MonthlySimpleInterestStrategy(), new YearlySimpleInterestStrategy())
        );
        FixedDepositService service = new FixedDepositService(
                repository,
                interestCalculationService,
                statusTransitionHandlers()
        );

        BookFDRequest request = new BookFDRequest();
        setField(request, "amount", new BigDecimal("4000"));
        setField(request, "interestScheme", InterestScheme.STANDARD_6_MONTHS);

        assertThrows(BusinessException.class, () -> service.bookFixedDeposit(1L, request));
    }

    @Test
    void bookFixedDeposit_savesAndReturnsFd() throws Exception {
        FixedDepositRepository repository = proxyRepository(new FixedDeposit());
        InterestCalculationService interestCalculationService = new InterestCalculationService(
                List.of(new MonthlySimpleInterestStrategy(), new YearlySimpleInterestStrategy())
        );
        FixedDepositService service = new FixedDepositService(
                repository,
                interestCalculationService,
                statusTransitionHandlers()
        );

        BookFDRequest request = new BookFDRequest();
        setField(request, "amount", new BigDecimal("5000"));
        setField(request, "interestScheme", InterestScheme.STANDARD_6_MONTHS);

        FixedDeposit saved = service.bookFixedDeposit(7L, request);

        assertEquals(7L, saved.getUserId());
        assertEquals(new BigDecimal("5000"), saved.getAmount());
        assertEquals(InterestScheme.STANDARD_6_MONTHS, saved.getInterestScheme());
    }

    @Test
    void getFixedDepositById_throwsWhenNotFound() {
        FixedDepositRepository repository = proxyRepository(null);
        InterestCalculationService interestCalculationService = new InterestCalculationService(
                List.of(new MonthlySimpleInterestStrategy(), new YearlySimpleInterestStrategy())
        );
        FixedDepositService service = new FixedDepositService(
                repository,
                interestCalculationService,
                statusTransitionHandlers()
        );

        assertThrows(ResourceNotFoundException.class, () -> service.getFixedDepositById(1L, 2L));
    }

    @Test
    void updateFixedDepositStatus_throwsWhenNotYetMatured() {
        FixedDeposit fd = new FixedDeposit();
        fd.setStatus(FDStatus.ACTIVE);
        fd.setMaturityDate(LocalDate.now().plusDays(1));
        fd.setInterestScheme(InterestScheme.STANDARD_6_MONTHS);

        FixedDepositRepository repository = proxyRepository(fd);
        InterestCalculationService interestCalculationService = new InterestCalculationService(
                List.of(new MonthlySimpleInterestStrategy(), new YearlySimpleInterestStrategy())
        );
        FixedDepositService service = new FixedDepositService(
                repository,
                interestCalculationService,
                statusTransitionHandlers()
        );

        assertThrows(BusinessException.class, () -> service.updateFixedDepositStatus(10L, FDStatus.MATURED));
    }

    @Test
    void getUserFDsMaturingWithinDays_throwsForInvalidDays() {
        FixedDepositRepository repository = proxyRepository(new FixedDeposit());
        InterestCalculationService interestCalculationService = new InterestCalculationService(
                List.of(new MonthlySimpleInterestStrategy(), new YearlySimpleInterestStrategy())
        );
        FixedDepositService service = new FixedDepositService(
                repository,
                interestCalculationService,
                statusTransitionHandlers()
        );

        assertThrows(BusinessException.class, () -> service.getUserFDsMaturingWithinDays(5L, 0));
    }

    private FixedDepositRepository proxyRepository(FixedDeposit fd) {
        return (FixedDepositRepository) Proxy.newProxyInstance(
                FixedDepositRepository.class.getClassLoader(),
                new Class<?>[]{FixedDepositRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("save")) {
                        return args[0];
                    }
                    if (method.getName().equals("findById")) {
                        return Optional.ofNullable(fd);
                    }
                    if (method.getName().equals("findByIdAndUserId")) {
                        return Optional.ofNullable(fd);
                    }
                    return null;
                }
        );
    }

    private void setField(Object target, String field, Object value) throws Exception {
        Field declaredField = target.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);
        declaredField.set(target, value);
    }

    private List<tech.zeta.Digital_Fixed_Deposit_System.service.fd.status.FDStatusTransitionHandler> statusTransitionHandlers() {
        return List.of(
                new ActiveToMaturedTransitionHandler(),
                new ActiveToBrokenTransitionHandler(),
                new MaturedToClosedTransitionHandler()
        );
    }
}
