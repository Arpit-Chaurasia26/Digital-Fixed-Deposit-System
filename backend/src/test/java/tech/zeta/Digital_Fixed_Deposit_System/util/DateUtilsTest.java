package tech.zeta.Digital_Fixed_Deposit_System.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Arpit Chaurasia
 */
public class DateUtilsTest {

    @Test
    void getFinancialYearStart_returnsAprilFirst() {
        LocalDate start = DateUtils.getFinancialYearStart(2024);
        assertEquals(LocalDate.of(2024, 4, 1), start);
    }

    @Test
    void getFinancialYearEnd_returnsMarchThirtyFirst() {
        LocalDate end = DateUtils.getFinancialYearEnd(2024);
        assertEquals(LocalDate.of(2025, 3, 31), end);
    }

    @Test
    void getCurrentFinancialYear_matchesCurrentDate() {
        LocalDate today = LocalDate.now();
        int expected = today.getMonthValue() >= 4 ? today.getYear() : today.getYear() - 1;

        assertEquals(expected, DateUtils.getCurrentFinancialYear());
    }
}
