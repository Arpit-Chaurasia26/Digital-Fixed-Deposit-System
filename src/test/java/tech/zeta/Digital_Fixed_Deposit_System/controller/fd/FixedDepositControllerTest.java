package tech.zeta.Digital_Fixed_Deposit_System.controller.fd;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.BookFDRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.FDMaturityResponse;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.FDPortfolioResponse;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.FDInterestResponse;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.FDFinancialYearSummaryResponse;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositService;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FixedDepositControllerTest {

    @Test
    void getAllSchemes_returnsList() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        ResponseEntity<List<tech.zeta.Digital_Fixed_Deposit_System.dto.fd.SchemeResponse>> response = controller.getAllSchemes();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void bookFixedDeposit_returnsCreated() throws Exception {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );
        BookFDRequest request = new BookFDRequest();
        setField(request, "amount", new BigDecimal("5000"));
        setField(request, "interestScheme", InterestScheme.STANDARD_6_MONTHS);

        ResponseEntity<FixedDeposit> response = controller.bookFixedDeposit(request);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getUserId());
    }

    @Test
    void getUserFixedDeposits_returnsOk() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        ResponseEntity<List<FixedDeposit>> response = controller.getUserFixedDeposits(10L, null, null, null);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    private static class FixedDepositServiceStub extends FixedDepositService {
        FixedDepositServiceStub() {
            super(null, null);
        }

        @Override
        public FixedDeposit bookFixedDeposit(Long userId, BookFDRequest request) {
            FixedDeposit fd = new FixedDeposit();
            fd.setUserId(userId);
            fd.setAmount(request.getAmount());
            fd.setInterestScheme(request.getInterestScheme());
            return fd;
        }

        @Override
        public List<FixedDeposit> getFixedDepositsByFilters(Long userId, FDStatus status, BigDecimal minAmount, BigDecimal maxAmount) {
            return Collections.emptyList();
        }

        @Override
        public FixedDeposit getFixedDepositById(Long userId, Long fdId) {
            FixedDeposit fd = new FixedDeposit();
            fd.setUserId(userId);
            fd.setInterestScheme(InterestScheme.STANDARD_6_MONTHS);
            fd.setStartDate(LocalDate.now().minusDays(10));
            fd.setMaturityDate(LocalDate.now().plusDays(10));
            fd.setInterestRate(new BigDecimal("5.50"));
            fd.setAmount(new BigDecimal("5000"));
            return fd;
        }

        @Override
        public List<FDMaturityResponse> getUserFDsMaturingWithinDays(Long userId, int days) {
            return List.of(new FDMaturityResponse(
                    1L,
                    userId,
                    new BigDecimal("5000"),
                    LocalDate.now().plusDays(days),
                    days,
                    FDStatus.ACTIVE
            ));
        }

        @Override
        public FDFinancialYearSummaryResponse getUserFinancialYearSummary(Long userId, Integer year) {
            return new FDFinancialYearSummaryResponse("2024-2025", 1, new BigDecimal("5000"), new BigDecimal("100"));
        }

        @Override
        public FDPortfolioResponse getUserFDPortfolio(Long userId) {
            return new FDPortfolioResponse(1, 1, 0, 0, new BigDecimal("5000"), new BigDecimal("100"), LocalDate.now().plusDays(10));
        }

        @Override
        public tech.zeta.Digital_Fixed_Deposit_System.dto.fd.FDInterestTimelineResponse getInterestTimeline(FixedDeposit fd, String interval) {
            return new tech.zeta.Digital_Fixed_Deposit_System.dto.fd.FDInterestTimelineResponse(
                    1L,
                    new BigDecimal("5000"),
                    new BigDecimal("5.50"),
                    interval,
                    List.of()
            );
        }

        @Override
        public FDInterestResponse getCurrentInterestForUser(Long userId, Long fdId) {
            return new FDInterestResponse(
                    fdId,
                    FDStatus.ACTIVE,
                    new BigDecimal("5000"),
                    new BigDecimal("10"),
                    new BigDecimal("5.50"),
                    InterestFrequency.MONTHLY,
                    LocalDate.now().minusDays(10),
                    LocalDate.now().plusDays(10)
            );
        }
    }

    private void setField(Object target, String field, Object value) throws Exception {
        Field declaredField = target.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);
        declaredField.set(target, value);
    }
}
