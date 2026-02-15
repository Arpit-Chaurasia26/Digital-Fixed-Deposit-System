package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.*;
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
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.status.FDStatusTransitionHandler;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.status.MaturedToClosedTransitionHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Arpit Chaurasia
 */
@ExtendWith(MockitoExtension.class)
public class FixedDepositServiceTest {

    @Mock private FixedDepositRepository fixedDepositRepository;
    @Mock private InterestCalculationService interestCalculationService;
    @Mock private FDStatusTransitionHandler transitionHandler;

    @InjectMocks
    private FixedDepositService service;

    private static BookFDRequest bookRequest(BigDecimal amount, InterestScheme scheme) throws Exception {
        BookFDRequest request = new BookFDRequest();
        setField(request, "amount", amount);
        setField(request, "interestScheme", scheme);
        return request;
    }

    @Nested
    @DisplayName("Book Fixed Deposit")
    class BookTests {
        @Test
        void throwsWhenAmountNull() throws Exception {
            assertThrows(BusinessException.class, () -> service.bookFixedDeposit(1L, bookRequest(null, InterestScheme.STANDARD_6_MONTHS)));
        }

        @Test
        void throwsWhenBelowMinimum() throws Exception {
            assertThrows(BusinessException.class, () -> service.bookFixedDeposit(1L, bookRequest(new BigDecimal("4000"), InterestScheme.STANDARD_6_MONTHS)));
        }

        @Test
        void throwsWhenSchemeMissing() throws Exception {
            assertThrows(BusinessException.class, () -> service.bookFixedDeposit(1L, bookRequest(new BigDecimal("5000"), null)));
        }

        @Test
        void savesAndReturnsFd() throws Exception {
            when(fixedDepositRepository.save(any(FixedDeposit.class))).thenAnswer(inv -> inv.getArgument(0));

            FixedDeposit saved = service.bookFixedDeposit(7L, bookRequest(new BigDecimal("5000"), InterestScheme.STANDARD_6_MONTHS));

            assertEquals(7L, saved.getUserId());
            assertEquals(new BigDecimal("5000"), saved.getAmount());
            assertEquals(InterestScheme.STANDARD_6_MONTHS, saved.getInterestScheme());
        }
    }

    @Test
    void getFixedDepositsByUser_enrichesAccruedInterest() {
        FixedDeposit fd = fd(1L, FDStatus.ACTIVE);
        when(fixedDepositRepository.findByUserId(1L)).thenReturn(List.of(fd));
        when(interestCalculationService.calculateAccruedInterest(fd)).thenReturn(new BigDecimal("10.00"));

        List<FixedDeposit> result = service.getFixedDepositsByUser(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAccruedInterest()).isEqualByComparingTo("10.00");
    }

    @Test
    void getFixedDepositById_throwsWhenNotFound() {
        when(fixedDepositRepository.findByIdAndUserId(2L, 1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getFixedDepositById(1L, 2L));
    }

    @Test
    void updateFixedDepositStatus_throwsWhenNotFound() {
        when(fixedDepositRepository.findById(10L)).thenReturn(java.util.Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateFixedDepositStatus(10L, FDStatus.MATURED));
    }

    @Test
    void updateFixedDepositStatus_throwsWhenNoHandler() {
        FixedDeposit fd = fd(1L, FDStatus.ACTIVE);
        when(fixedDepositRepository.findById(1L)).thenReturn(java.util.Optional.of(fd));
        when(transitionHandler.supports(any(), any())).thenReturn(false);
        service = new FixedDepositService(fixedDepositRepository, interestCalculationService, List.of(transitionHandler));

        assertThrows(BusinessException.class, () -> service.updateFixedDepositStatus(1L, FDStatus.MATURED));
    }

    @Test
    void updateFixedDepositStatus_appliesHandlerAndSaves() {
        FixedDeposit fd = fd(1L, FDStatus.ACTIVE);
        when(fixedDepositRepository.findById(1L)).thenReturn(java.util.Optional.of(fd));
        when(transitionHandler.supports(FDStatus.ACTIVE, FDStatus.MATURED)).thenReturn(true);
        when(fixedDepositRepository.save(any(FixedDeposit.class))).thenAnswer(inv -> inv.getArgument(0));
        service = new FixedDepositService(fixedDepositRepository, interestCalculationService, List.of(transitionHandler));

        FixedDeposit updated = service.updateFixedDepositStatus(1L, FDStatus.MATURED);

        verify(transitionHandler).apply(fd);
        assertThat(updated).isSameAs(fd);
    }

    @Nested
    @DisplayName("Filter Queries")
    class FilterTests {
        @Test
        void statusAndAmountFilters() {
            when(fixedDepositRepository.findByUserIdAndStatusAndAmountBetween(eq(1L), eq(FDStatus.ACTIVE), any(), any()))
                    .thenReturn(List.of(fd(1L, FDStatus.ACTIVE)));

            List<FixedDeposit> result = service.getFixedDepositsByFilters(1L, FDStatus.ACTIVE, new BigDecimal("5000"), new BigDecimal("8000"));
            assertThat(result).hasSize(1);
        }

        @Test
        void statusOnlyFilter() {
            when(fixedDepositRepository.findByUserIdAndStatus(1L, FDStatus.MATURED))
                    .thenReturn(List.of(fd(1L, FDStatus.MATURED)));

            List<FixedDeposit> result = service.getFixedDepositsByFilters(1L, FDStatus.MATURED, null, null);
            assertThat(result).hasSize(1);
        }

        @Test
        void amountOnlyFilter() {
            when(fixedDepositRepository.findByUserIdAndAmountBetween(eq(1L), any(), any()))
                    .thenReturn(List.of(fd(1L, FDStatus.ACTIVE)));

            List<FixedDeposit> result = service.getFixedDepositsByFilters(1L, null, new BigDecimal("5000"), null);
            assertThat(result).hasSize(1);
        }

        @Test
        void noFilters() {
            when(fixedDepositRepository.findByUserId(1L)).thenReturn(List.of(fd(1L, FDStatus.ACTIVE)));
            List<FixedDeposit> result = service.getFixedDepositsByFilters(1L, null, null, null);
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Maturity Queries")
    class MaturityTests {
        @Test
        void userMaturing_invalidDays() {
            assertThrows(BusinessException.class, () -> service.getUserFDsMaturingWithinDays(1L, 0));
        }

        @Test
        void userMaturing_filtersStatuses() {
            FixedDeposit active = fd(1L, FDStatus.ACTIVE);
            FixedDeposit closed = fd(1L, FDStatus.CLOSED);
            when(fixedDepositRepository.findByUserIdAndMaturityDateBetween(anyLong(), any(), any()))
                    .thenReturn(List.of(active, closed));

            List<FDMaturityResponse> result = service.getUserFDsMaturingWithinDays(1L, 30);
            assertThat(result).hasSize(1);
        }

        @Test
        void adminMaturing_invalidDays() {
            assertThrows(BusinessException.class, () -> service.getAllFDsMaturingWithinDays(0));
        }
    }

    @Test
    void getAdminFDsByFinancialYear_enrichesInterest() {
        FixedDeposit fd = fd(1L, FDStatus.ACTIVE);
        when(fixedDepositRepository.findByCreatedAtBetween(any(), any())).thenReturn(List.of(fd));
        when(interestCalculationService.calculateAccruedInterest(fd)).thenReturn(new BigDecimal("9.00"));

        List<FixedDeposit> result = service.getAdminFDsByFinancialYear(null);

        assertThat(result.get(0).getAccruedInterest()).isEqualByComparingTo("9.00");
    }

    @Test
    void getAllFDsChronological_usesSortOrder() {
        when(fixedDepositRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

        service.getAllFDsChronological("asc");
        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(fixedDepositRepository).findAll(sortCaptor.capture());
        assertThat(sortCaptor.getValue().getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void getUserFDPortfolio_computesCountsAndTotals() {
        FixedDeposit active = fd(1L, FDStatus.ACTIVE);
        FixedDeposit matured = fd(1L, FDStatus.MATURED);
        FixedDeposit broken = fd(1L, FDStatus.BROKEN);
        when(fixedDepositRepository.findByUserId(1L)).thenReturn(List.of(active, matured, broken));
        when(interestCalculationService.calculateAccruedInterest(any(FixedDeposit.class))).thenReturn(new BigDecimal("5.00"));

        FDPortfolioResponse response = service.getUserFDPortfolio(1L);

        assertThat(response.getTotalFDs()).isEqualTo(3);
        assertThat(response.getActiveFDs()).isEqualTo(1);
        assertThat(response.getMaturedFDs()).isEqualTo(1);
        assertThat(response.getBrokenFDs()).isEqualTo(1);
        assertThat(response.getTotalInterestAccrued()).isEqualByComparingTo("15.00");
        assertThat(response.getNextMaturityDate()).isNotNull();
    }

    @Test
    void getFixedDepositByIdForAdmin_notFound() {
        when(fixedDepositRepository.findById(99L)).thenReturn(java.util.Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getFixedDepositByIdForAdmin(99L));
    }

    @Test
    void getCurrentInterestForUser_notFound() {
        when(fixedDepositRepository.findByIdAndUserId(5L, 1L)).thenReturn(java.util.Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getCurrentInterestForUser(1L, 5L));
    }

    @Test
    void getInterestTimeline_yearlyAndMonthlyBranches() {
        FixedDeposit fd = fd(1L, FDStatus.ACTIVE);
        fd.setStartDate(LocalDate.now().minusMonths(3));
        fd.setMaturityDate(LocalDate.now().plusMonths(1));
        when(interestCalculationService.calculateAccruedInterest(any(FixedDeposit.class))).thenReturn(new BigDecimal("1.00"));

        FDInterestTimelineResponse monthly = service.getInterestTimeline(fd, "MONTHLY");
        FDInterestTimelineResponse yearly = service.getInterestTimeline(fd, "YEARLY");

        assertThat(monthly.getTimeline()).isNotEmpty();
        assertThat(yearly.getTimeline()).isNotEmpty();
    }

    @Test
    void getCurrentInterestForUser_success() {
        FixedDeposit fd = fd(1L, FDStatus.ACTIVE);
        when(fixedDepositRepository.findByIdAndUserId(1L, 1L)).thenReturn(java.util.Optional.of(fd));
        when(interestCalculationService.calculateAccruedInterest(fd)).thenReturn(new BigDecimal("12.00"));

        FDInterestResponse response = service.getCurrentInterestForUser(1L, 1L);

        assertThat(response.getAccruedInterest()).isEqualByComparingTo("12.00");
    }

    private static FixedDeposit fd(Long userId, FDStatus status) {
        FixedDeposit fd = new FixedDeposit();
        fd.setUserId(userId);
        fd.setAmount(new BigDecimal("5000"));
        fd.setInterestScheme(InterestScheme.STANDARD_6_MONTHS);
        fd.setInterestRate(new BigDecimal("5.50"));
        fd.setTenureMonths(6);
        fd.setStartDate(LocalDate.now().minusMonths(1));
        fd.setMaturityDate(LocalDate.now().plusMonths(5));
        fd.setStatus(status);
        return fd;
    }

    private static void setField(Object target, String field, Object value) throws Exception {
        java.lang.reflect.Field declaredField = target.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);
        declaredField.set(target, value);
    }
}
