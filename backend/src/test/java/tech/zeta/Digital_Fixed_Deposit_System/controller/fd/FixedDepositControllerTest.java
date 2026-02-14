package tech.zeta.Digital_Fixed_Deposit_System.controller.fd;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.*;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

// Author - Arpit Chaurasia
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

    @Test
    void getUserFixedDeposits_unauthorized() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        assertThrows(tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException.class,
                () -> controller.getUserFixedDeposits(11L, null, null, null));
    }

    @Test
    void getFixedDepositById_returnsOk() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        ResponseEntity<FixedDeposit> response = controller.getFixedDepositById(10L, 5L);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getFixedDepositById_unauthorized() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        assertThrows(tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException.class,
                () -> controller.getFixedDepositById(11L, 5L));
    }

    @Test
    void getUserMaturingFDs_returnsOk() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        ResponseEntity<List<FDMaturityResponse>> response = controller.getUserMaturingFDs(10L, 7);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getUserMaturingFDs_unauthorized() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        assertThrows(tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException.class,
                () -> controller.getUserMaturingFDs(11L, 7));
    }

    @Test
    void getUserFinancialYearSummary_returnsOk() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        ResponseEntity<FDFinancialYearSummaryResponse> response = controller.getUserFinancialYearSummary(10L, 2025);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getUserFinancialYearSummary_unauthorized() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        assertThrows(tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException.class,
                () -> controller.getUserFinancialYearSummary(11L, 2025));
    }

    @Test
    void getUserFDPortfolio_returnsOk() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        ResponseEntity<FDPortfolioResponse> response = controller.getUserFDPortfolio(10L);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getUserFDPortfolio_unauthorized() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        assertThrows(tech.zeta.Digital_Fixed_Deposit_System.exception.UnauthorizedException.class,
                () -> controller.getUserFDPortfolio(11L));
    }

    @Test
    void getInterestTimeline_withIntervalAndDefault() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        ResponseEntity<FDInterestTimelineResponse> monthly = controller.getInterestTimeline(5L, "MONTHLY");
        ResponseEntity<FDInterestTimelineResponse> defaultInterval = controller.getInterestTimeline(5L, null);

        assertEquals(200, monthly.getStatusCode().value());
        assertEquals(200, defaultInterval.getStatusCode().value());
        assertNotNull(monthly.getBody());
        assertNotNull(defaultInterval.getBody());
    }

    @Test
    void getAccruedInterest_returnsOk() {
        FixedDepositController controller = new FixedDepositController(
                new FixedDepositServiceStub(),
                new CurrentUserProvider() {
                    @Override
                    public Long getCurrentUserId() {
                        return 10L;
                    }
                }
        );

        ResponseEntity<FDInterestResponse> response = controller.getAccruedInterest(5L);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    private static class FixedDepositServiceStub extends FixedDepositService {
        FixedDepositServiceStub() {
            super(null, null, null);
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
