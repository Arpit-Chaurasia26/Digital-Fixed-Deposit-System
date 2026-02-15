package tech.zeta.Digital_Fixed_Deposit_System.service.withdrawal;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalEligibility;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalHistory;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalPreview;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalReceipt;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;
import tech.zeta.Digital_Fixed_Deposit_System.entity.transaction.Transaction;
import tech.zeta.Digital_Fixed_Deposit_System.exception.AccountNotFoundException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.InSufficientFundsException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.InvalidOperationException;
import tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;
import tech.zeta.Digital_Fixed_Deposit_System.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("WithdrawalServiceImpl Unit Tests - Full Coverage")
class WithdrawalServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(WithdrawalServiceImplTest.class);

    @Mock private FixedDepositRepository fixedDepositRepository;
    @Mock private CurrentUserProvider currentUserProvider;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks
    private WithdrawalServiceImpl withdrawalService;

    private static final Long USER_ID = 1L;
    private static final Long FD_ID = 100L;
    private static final BigDecimal PRINCIPAL = new BigDecimal("100000.00");
    private static final BigDecimal WITHDRAWAL_AMOUNT = new BigDecimal("50000.00");

    private FixedDeposit testFD;

    @BeforeEach
    void setUp() {
        testFD = createTestFD();
        when(currentUserProvider.getCurrentUserId()).thenReturn(USER_ID);
    }

    @Nested
    @DisplayName("Withdrawal Preview Tests")
    class WithdrawalPreviewTests {

        @Test
        @DisplayName("Should generate preview for matured FD")
        void getPreview_ForMaturedFD_ShouldSucceed() {
            testFD.setMaturityDate(LocalDate.now().minusDays(10));
            testFD.setStatus(FDStatus.MATURED);
            when(fixedDepositRepository.findById(FD_ID)).thenReturn(Optional.of(testFD));

            WithdrawalPreview preview = withdrawalService.getWithdrawalPreview(FD_ID, WITHDRAWAL_AMOUNT);

            assertThat(preview).isNotNull();
            assertThat(preview.getWithdrawalAmount()).isEqualByComparingTo(WITHDRAWAL_AMOUNT);
            assertThat(preview.getPenalty()).isEqualByComparingTo(BigDecimal.ZERO);
            logger.info("Matured FD preview test passed");
        }

        @Test
        @DisplayName("Should generate preview with penalty for premature withdrawal")
        void getPreview_ForPrematureWithdrawal_ShouldApplyPenalty() {
            testFD.setStartDate(LocalDate.now().minusMonths(6));
            testFD.setMaturityDate(LocalDate.now().plusMonths(6));
            testFD.setStatus(FDStatus.ACTIVE);
            testFD.setInterestScheme(InterestScheme.STANDARD_12_MONTHS);
            when(fixedDepositRepository.findById(FD_ID)).thenReturn(Optional.of(testFD));

            WithdrawalPreview preview = withdrawalService.getWithdrawalPreview(FD_ID, WITHDRAWAL_AMOUNT);

            assertThat(preview).isNotNull();
            assertThat(preview.getPenalty()).isGreaterThan(BigDecimal.ZERO);
            logger.info("Premature withdrawal penalty test passed");
        }

        @Test
        @DisplayName("Should reject negative withdrawal amount")
        void getPreview_NegativeAmount_ShouldThrow() {
            assertThatThrownBy(() -> withdrawalService.getWithdrawalPreview(FD_ID, new BigDecimal("-1000")))
                    .isInstanceOf(ValidationException.class);
            logger.info("Negative amount test passed");
        }

        @Test
        @DisplayName("Should reject zero withdrawal amount")
        void getPreview_ZeroAmount_ShouldThrow() {
            assertThatThrownBy(() -> withdrawalService.getWithdrawalPreview(FD_ID, BigDecimal.ZERO))
                    .isInstanceOf(ValidationException.class);
            logger.info("Zero amount test passed");
        }

        @Test
        @DisplayName("Should reject amount greater than principal")
        void getPreview_ExcessAmount_ShouldThrow() {
            when(fixedDepositRepository.findById(FD_ID)).thenReturn(Optional.of(testFD));
            BigDecimal excessAmount = PRINCIPAL.add(new BigDecimal("1000"));

            assertThatThrownBy(() -> withdrawalService.getWithdrawalPreview(FD_ID, excessAmount))
                    .isInstanceOf(InSufficientFundsException.class);
            logger.info("Excess amount test passed");
        }

        @Test
        @DisplayName("Should reject withdrawal from closed FD")
        void getPreview_ClosedFD_ShouldThrow() {
            testFD.setStatus(FDStatus.CLOSED);
            when(fixedDepositRepository.findById(FD_ID)).thenReturn(Optional.of(testFD));

            assertThatThrownBy(() -> withdrawalService.getWithdrawalPreview(FD_ID, WITHDRAWAL_AMOUNT))
                    .isInstanceOf(InvalidOperationException.class);
            logger.info("Closed FD test passed");
        }

        @Test
        @DisplayName("Should throw when FD not found")
        void getPreview_FDNotFound_ShouldThrow() {
            when(fixedDepositRepository.findById(FD_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> withdrawalService.getWithdrawalPreview(FD_ID, WITHDRAWAL_AMOUNT))
                    .isInstanceOf(AccountNotFoundException.class);
            logger.info("FD not found test passed");
        }

        @Test
        @DisplayName("Should reject unauthorized access")
        void getPreview_UnauthorizedUser_ShouldThrow() {
            testFD.setUserId(999L);
            when(fixedDepositRepository.findById(FD_ID)).thenReturn(Optional.of(testFD));

            assertThatThrownBy(() -> withdrawalService.getWithdrawalPreview(FD_ID, WITHDRAWAL_AMOUNT))
                    .isInstanceOf(UnauthorizedException.class);
            logger.info("Unauthorized access test passed");
        }
    }

    @Nested
    @DisplayName("Confirm Withdrawal Tests")
    class ConfirmWithdrawalTests {

        @Test
        @DisplayName("Should confirm full withdrawal and close FD")
        void confirmWithdrawal_FullAmount_ShouldCloseFD() {
            testFD.setMaturityDate(LocalDate.now().minusDays(1));
            testFD.setStatus(FDStatus.MATURED);
            when(fixedDepositRepository.findById(FD_ID)).thenReturn(Optional.of(testFD));
            when(fixedDepositRepository.save(any())).thenReturn(testFD);
            when(transactionRepository.save(any())).thenReturn(new Transaction());

            WithdrawalReceipt receipt = withdrawalService.confirmWithdrawal(FD_ID, PRINCIPAL);

            assertThat(receipt).isNotNull();
            assertThat(testFD.getStatus()).isEqualTo(FDStatus.CLOSED);
            verify(transactionRepository).save(any(Transaction.class));
            logger.info("Full withdrawal test passed");
        }

        @Test
        @DisplayName("Should confirm partial withdrawal")
        void confirmWithdrawal_PartialAmount_ShouldReducePrincipal() {
            testFD.setMaturityDate(LocalDate.now().minusDays(1));
            testFD.setStatus(FDStatus.MATURED);
            when(fixedDepositRepository.findById(FD_ID)).thenReturn(Optional.of(testFD));
            when(fixedDepositRepository.save(any())).thenReturn(testFD);
            when(transactionRepository.save(any())).thenReturn(new Transaction());

            WithdrawalReceipt receipt = withdrawalService.confirmWithdrawal(FD_ID, WITHDRAWAL_AMOUNT);

            assertThat(receipt).isNotNull();
            assertThat(testFD.getAmount()).isEqualByComparingTo(PRINCIPAL.subtract(WITHDRAWAL_AMOUNT));
            logger.info("Partial withdrawal test passed");
        }

        @Test
        @DisplayName("Should set status to BROKEN for premature full withdrawal")
        void confirmWithdrawal_PrematureFull_ShouldSetBroken() {
            testFD.setStartDate(LocalDate.now().minusMonths(6));
            testFD.setMaturityDate(LocalDate.now().plusMonths(6));
            testFD.setStatus(FDStatus.ACTIVE);
            testFD.setInterestScheme(InterestScheme.STANDARD_12_MONTHS);
            when(fixedDepositRepository.findById(FD_ID)).thenReturn(Optional.of(testFD));
            when(fixedDepositRepository.save(any())).thenReturn(testFD);
            when(transactionRepository.save(any())).thenReturn(new Transaction());

            WithdrawalReceipt receipt = withdrawalService.confirmWithdrawal(FD_ID, PRINCIPAL);

            assertThat(receipt).isNotNull();
            assertThat(testFD.getStatus()).isEqualTo(FDStatus.BROKEN);
            logger.info("Premature full withdrawal test passed");
        }
    }

    @Nested
    @DisplayName("Withdrawal Eligibility Tests")
    class WithdrawalEligibilityTests {

        @Test
        @DisplayName("Should return eligible for matured FD")
        void checkEligibility_MaturedFD_ShouldBeEligible() {
            testFD.setMaturityDate(LocalDate.now().minusDays(1));
            testFD.setStatus(FDStatus.MATURED);
            when(fixedDepositRepository.findById(FD_ID)).thenReturn(Optional.of(testFD));

            WithdrawalEligibility eligibility = withdrawalService.checkWithdrawalEligibility(FD_ID);

            assertThat(eligibility.isEligible()).isTrue();
            assertThat(eligibility.getRootCause()).contains("matured");
            logger.info("Matured FD eligibility test passed");
        }

        @Test
        @DisplayName("Should return eligible with warning for premature break allowed")
        void checkEligibility_PrematureAllowed_ShouldBeEligibleWithWarning() {
            testFD.setMaturityDate(LocalDate.now().plusMonths(6));
            testFD.setStatus(FDStatus.ACTIVE);
            testFD.setInterestScheme(InterestScheme.STANDARD_12_MONTHS);
            when(fixedDepositRepository.findById(FD_ID)).thenReturn(Optional.of(testFD));

            WithdrawalEligibility eligibility = withdrawalService.checkWithdrawalEligibility(FD_ID);

            assertThat(eligibility.isEligible()).isTrue();
            assertThat(eligibility.getRootCause()).contains("WARNING");
            logger.info("Premature with warning test passed");
        }

        @Test
        @DisplayName("Should return not eligible for closed FD")
        void checkEligibility_ClosedFD_ShouldNotBeEligible() {
            testFD.setStatus(FDStatus.CLOSED);
            when(fixedDepositRepository.findById(FD_ID)).thenReturn(Optional.of(testFD));

            WithdrawalEligibility eligibility = withdrawalService.checkWithdrawalEligibility(FD_ID);

            assertThat(eligibility.isEligible()).isFalse();
            assertThat(eligibility.getRootCause()).contains("Closed");
            logger.info("Closed FD eligibility test passed");
        }
    }

    @Nested
    @DisplayName("Withdrawal History Tests")
    class WithdrawalHistoryTests {

        @Test
        @DisplayName("Should return withdrawal history for user")
        void getHistory_ShouldReturnList() {
            Transaction transaction = new Transaction();
            transaction.setFdId(FD_ID);
            transaction.setAmountPaid(WITHDRAWAL_AMOUNT);
            transaction.setInterestPaid(new BigDecimal("500.00"));
            setField(transaction, "createdAt", LocalDateTime.now());
            when(transactionRepository.findByUserId(USER_ID)).thenReturn(List.of(transaction));

            List<WithdrawalHistory> history = withdrawalService.getWithdrawalHistory(USER_ID);

            assertThat(history).hasSize(1);
            assertThat(history.get(0).getFdId()).isEqualTo(FD_ID);
            logger.info("Withdrawal history test passed");
        }

        @Test
        @DisplayName("Should reject unauthorized history access")
        void getHistory_UnauthorizedUser_ShouldThrow() {
            assertThatThrownBy(() -> withdrawalService.getWithdrawalHistory(999L))
                    .isInstanceOf(UnauthorizedException.class);
            logger.info("Unauthorized history test passed");
        }

        @Test
        @DisplayName("Should return empty list when no history")
        void getHistory_NoTransactions_ShouldReturnEmpty() {
            when(transactionRepository.findByUserId(USER_ID)).thenReturn(Collections.emptyList());

            List<WithdrawalHistory> history = withdrawalService.getWithdrawalHistory(USER_ID);

            assertThat(history).isEmpty();
            logger.info("Empty history test passed");
        }
    }

    // Helper methods
    private FixedDeposit createTestFD() {
        FixedDeposit fd = new FixedDeposit();
        setField(fd, "id", FD_ID);
        fd.setUserId(USER_ID);
        fd.setAmount(PRINCIPAL);
        fd.setInterestScheme(InterestScheme.STANDARD_12_MONTHS);
        fd.setInterestRate(new BigDecimal("6.50"));
        fd.setTenureMonths(12);
        fd.setStartDate(LocalDate.now().minusMonths(12));
        fd.setMaturityDate(LocalDate.now());
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
