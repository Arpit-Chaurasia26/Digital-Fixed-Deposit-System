package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestFrequency;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Arpit Chaurasia
 */
public class FdDtoTest {

    @Test
    void fdInterestResponse_gettersReturnValues() {
        FDInterestResponse response = new FDInterestResponse(
                1L,
                FDStatus.ACTIVE,
                new BigDecimal("5000"),
                new BigDecimal("12.34"),
                new BigDecimal("5.50"),
                InterestFrequency.MONTHLY,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1)
        );

        assertEquals(1L, response.getFdId());
        assertEquals(FDStatus.ACTIVE, response.getStatus());
        assertEquals(new BigDecimal("5000"), response.getAmount());
        assertEquals(new BigDecimal("12.34"), response.getAccruedInterest());
        assertEquals(new BigDecimal("5.50"), response.getInterestRate());
        assertEquals(InterestFrequency.MONTHLY, response.getInterestFrequency());
        assertNotNull(response.getStartDate());
        assertNotNull(response.getMaturityDate());
    }

    @Test
    void fdPortfolioResponse_gettersReturnValues() {
        FDPortfolioResponse response = new FDPortfolioResponse(
                3,
                1,
                1,
                1,
                new BigDecimal("10000"),
                new BigDecimal("250"),
                LocalDate.now().plusDays(7)
        );

        assertEquals(3, response.getTotalFDs());
        assertEquals(1, response.getActiveFDs());
        assertEquals(1, response.getMaturedFDs());
        assertEquals(1, response.getBrokenFDs());
        assertEquals(new BigDecimal("10000"), response.getTotalPrincipal());
        assertEquals(new BigDecimal("250"), response.getTotalInterestAccrued());
        assertNotNull(response.getNextMaturityDate());
    }

    @Test
    void maturityResponse_gettersReturnValues() {
        FDMaturityResponse response = new FDMaturityResponse(
                1L,
                2L,
                new BigDecimal("5000"),
                LocalDate.now().plusDays(3),
                3,
                FDStatus.ACTIVE
        );

        assertEquals(1L, response.getFdId());
        assertEquals(2L, response.getUserId());
        assertEquals(3, response.getDaysRemaining());
        assertEquals(FDStatus.ACTIVE, response.getStatus());
    }

    @Test
    void financialYearSummaryResponse_gettersReturnValues() {
        FDFinancialYearSummaryResponse response = new FDFinancialYearSummaryResponse(
                "2024-2025",
                5,
                new BigDecimal("25000"),
                new BigDecimal("1200")
        );

        assertEquals("2024-2025", response.getFinancialYear());
        assertEquals(5, response.getTotalFDsCreated());
        assertEquals(new BigDecimal("25000"), response.getTotalPrincipalDeposited());
        assertEquals(new BigDecimal("1200"), response.getTotalInterestAccruedTillDate());
    }

    @Test
    void interestTimelineResponse_gettersReturnValues() {
        InterestTimelinePoint point = new InterestTimelinePoint("2024-01", new BigDecimal("12.34"));
        FDInterestTimelineResponse response = new FDInterestTimelineResponse(
                9L,
                new BigDecimal("7000"),
                new BigDecimal("6.50"),
                "MONTHLY",
                List.of(point)
        );

        assertEquals(9L, response.getFdId());
        assertEquals(new BigDecimal("7000"), response.getPrincipal());
        assertEquals(new BigDecimal("6.50"), response.getInterestRate());
        assertEquals("MONTHLY", response.getInterval());
        assertEquals(1, response.getTimeline().size());
        assertEquals("2024-01", response.getTimeline().get(0).getPeriod());
        assertEquals(new BigDecimal("12.34"), response.getTimeline().get(0).getAccruedInterest());
    }

    @Test
    void interestTimelinePoint_gettersReturnValues() {
        InterestTimelinePoint point = new InterestTimelinePoint("2024-02", new BigDecimal("9.99"));

        assertEquals("2024-02", point.getPeriod());
        assertEquals(new BigDecimal("9.99"), point.getAccruedInterest());
    }

    @Test
    void schemeResponse_recordComponents() {
        SchemeResponse response = new SchemeResponse(
                "STANDARD_6_MONTHS",
                new BigDecimal("5.50"),
                6,
                "MONTHLY",
                true
        );

        assertEquals("STANDARD_6_MONTHS", response.name());
        assertEquals(6, response.tenureInMonths());
        assertTrue(response.prematureBreakAllowed());
    }

    @Test
    void bookRequest_hasValidationAnnotations() throws Exception {
        Field amount = BookFDRequest.class.getDeclaredField("amount");
        Field scheme = BookFDRequest.class.getDeclaredField("interestScheme");

        assertNotNull(amount.getAnnotation(NotNull.class));
        assertNotNull(amount.getAnnotation(Positive.class));
        assertNotNull(scheme.getAnnotation(NotNull.class));
    }

    @Test
    void updateStatusRequest_hasValidationAnnotation() throws Exception {
        Field status = UpdateFDStatusRequest.class.getDeclaredField("status");

        assertNotNull(status.getAnnotation(NotNull.class));
    }
}
