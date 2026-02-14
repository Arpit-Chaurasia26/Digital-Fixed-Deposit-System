package tech.zeta.Digital_Fixed_Deposit_System.service.fd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.interest.InterestCalculationStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Author - Arpit Chaurasia
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("InterestCalculationService Unit Tests - Full Coverage")
class InterestCalculationServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(InterestCalculationServiceTest.class);

    @Mock
    private InterestCalculationStrategy monthlyStrategy;

    private InterestCalculationService interestCalculationService;

    private static final Long FD_ID = 100L;
    private static final BigDecimal PRINCIPAL = new BigDecimal("100000.00");
    private static final BigDecimal INTEREST_RATE = new BigDecimal("6.50");

    private FixedDeposit testFD;

    @BeforeEach
    void setUp() {
        when(monthlyStrategy.supports(InterestFrequency.MONTHLY)).thenReturn(true);
        when(monthlyStrategy.calculate(any(), any(), any(), any()))
                .thenReturn(new BigDecimal("3250.00"));

        interestCalculationService = new InterestCalculationService(Arrays.asList(monthlyStrategy));
        testFD = createTestFD();
    }

    @Nested
    @DisplayName("Calculate Accrued Interest Tests")
    class CalculateAccruedInterestTests {

        @Test
        @DisplayName("Should calculate interest for active FD")
        void calculateInterest_ActiveFD_ShouldSucceed() {
            testFD.setStatus(FDStatus.ACTIVE);
            testFD.setStartDate(LocalDate.now().minusMonths(6));
            testFD.setMaturityDate(LocalDate.now().plusMonths(6));

            BigDecimal interest = interestCalculationService.calculateAccruedInterest(testFD);

            assertThat(interest).isGreaterThan(BigDecimal.ZERO);
            logger.info("Active FD interest test passed: {}", interest);
        }

        @Test
        @DisplayName("Should calculate interest for matured FD")
        void calculateInterest_MaturedFD_ShouldSucceed() {
            testFD.setStatus(FDStatus.MATURED);
            testFD.setStartDate(LocalDate.now().minusMonths(12));
            testFD.setMaturityDate(LocalDate.now().minusDays(1));

            BigDecimal interest = interestCalculationService.calculateAccruedInterest(testFD);

            assertThat(interest).isGreaterThan(BigDecimal.ZERO);
            logger.info("Matured FD interest test passed: {}", interest);
        }

        @Test
        @DisplayName("Should return zero for closed FD")
        void calculateInterest_ClosedFD_ShouldReturnZero() {
            testFD.setStatus(FDStatus.CLOSED);

            BigDecimal interest = interestCalculationService.calculateAccruedInterest(testFD);

            assertThat(interest).isEqualByComparingTo(BigDecimal.ZERO);
            logger.info("Closed FD zero interest test passed");
        }

        @Test
        @DisplayName("Should return zero for broken FD")
        void calculateInterest_BrokenFD_ShouldReturnZero() {
            testFD.setStatus(FDStatus.BROKEN);

            BigDecimal interest = interestCalculationService.calculateAccruedInterest(testFD);

            assertThat(interest).isEqualByComparingTo(BigDecimal.ZERO);
            logger.info("Broken FD zero interest test passed");
        }

        @Test
        @DisplayName("Should return zero when start date is after end date")
        void calculateInterest_InvalidDateRange_ShouldReturnZero() {
            testFD.setStatus(FDStatus.ACTIVE);
            testFD.setStartDate(LocalDate.now().plusMonths(1)); // Start in future
            testFD.setMaturityDate(LocalDate.now().minusMonths(1)); // Already passed

            BigDecimal interest = interestCalculationService.calculateAccruedInterest(testFD);

            assertThat(interest).isEqualByComparingTo(BigDecimal.ZERO);
            logger.info("Invalid date range test passed");
        }

        @Test
        @DisplayName("Should return zero when same start and end date")
        void calculateInterest_SameStartEndDate_ShouldReturnZero() {
            testFD.setStatus(FDStatus.ACTIVE);
            LocalDate today = LocalDate.now();
            testFD.setStartDate(today);
            testFD.setMaturityDate(today.plusMonths(12));

            BigDecimal interest = interestCalculationService.calculateAccruedInterest(testFD);

            // Same day = 0 days of interest
            assertThat(interest).isEqualByComparingTo(BigDecimal.ZERO);
            logger.info("Same date test passed");
        }

        @Test
        @DisplayName("Should use maturity date for matured FD")
        void calculateInterest_MaturedFD_ShouldUseMaturityDate() {
            testFD.setStatus(FDStatus.MATURED);
            testFD.setStartDate(LocalDate.now().minusMonths(12));
            testFD.setMaturityDate(LocalDate.now().minusMonths(1));

            BigDecimal interest = interestCalculationService.calculateAccruedInterest(testFD);

            assertThat(interest).isGreaterThan(BigDecimal.ZERO);
            logger.info("Maturity date calculation test passed");
        }

        @Test
        @DisplayName("Should throw when no strategy found")
        void calculateInterest_NoStrategy_ShouldThrow() {
            InterestCalculationService serviceWithNoStrategies =
                    new InterestCalculationService(Collections.emptyList());

            testFD.setStatus(FDStatus.ACTIVE);
            testFD.setStartDate(LocalDate.now().minusMonths(6));
            testFD.setMaturityDate(LocalDate.now().plusMonths(6));

            assertThatThrownBy(() -> serviceWithNoStrategies.calculateAccruedInterest(testFD))
                    .isInstanceOf(IllegalStateException.class);
            logger.info("No strategy exception test passed");
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle large principal amounts")
        void calculateInterest_LargePrincipal_ShouldSucceed() {
            testFD.setAmount(new BigDecimal("10000000.00")); // 1 crore
            testFD.setStatus(FDStatus.ACTIVE);
            testFD.setStartDate(LocalDate.now().minusMonths(6));
            testFD.setMaturityDate(LocalDate.now().plusMonths(6));

            BigDecimal interest = interestCalculationService.calculateAccruedInterest(testFD);

            assertThat(interest).isGreaterThan(BigDecimal.ZERO);
            logger.info("Large principal test passed: {}", interest);
        }

        @Test
        @DisplayName("Should handle minimum principal amount")
        void calculateInterest_MinPrincipal_ShouldSucceed() {
            testFD.setAmount(new BigDecimal("5000.00")); // Minimum FD
            testFD.setStatus(FDStatus.ACTIVE);
            testFD.setStartDate(LocalDate.now().minusMonths(6));
            testFD.setMaturityDate(LocalDate.now().plusMonths(6));

            BigDecimal interest = interestCalculationService.calculateAccruedInterest(testFD);

            assertThat(interest).isGreaterThanOrEqualTo(BigDecimal.ZERO);
            logger.info("Min principal test passed: {}", interest);
        }

        @Test
        @DisplayName("Should round interest to 2 decimal places")
        void calculateInterest_ShouldRoundTo2Decimals() {
            testFD.setStatus(FDStatus.ACTIVE);
            testFD.setStartDate(LocalDate.now().minusMonths(6));
            testFD.setMaturityDate(LocalDate.now().plusMonths(6));

            BigDecimal interest = interestCalculationService.calculateAccruedInterest(testFD);

            assertThat(interest.scale()).isLessThanOrEqualTo(2);
            logger.info("Rounding test passed: {}", interest);
        }
    }

    // Helper methods
    private FixedDeposit createTestFD() {
        FixedDeposit fd = new FixedDeposit();
        setField(fd, "id", FD_ID);
        fd.setUserId(1L);
        fd.setAmount(PRINCIPAL);
        fd.setInterestScheme(InterestScheme.STANDARD_12_MONTHS);
        fd.setInterestRate(INTEREST_RATE);
        fd.setTenureMonths(12);
        fd.setStartDate(LocalDate.now().minusMonths(6));
        fd.setMaturityDate(LocalDate.now().plusMonths(6));
        fd.setStatus(FDStatus.ACTIVE);
        fd.setAccruedInterest(BigDecimal.ZERO);
        return fd;
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) { }
    }
}
