package tech.zeta.Digital_Fixed_Deposit_System.util;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arpit Chaurasia
 */
@Slf4j
public final class DateUtils {

    private DateUtils() {}

    /**
     * @author Arpit Chaurasia
     */
    public static LocalDate getFinancialYearStart(int year) {
        LocalDate start = LocalDate.of(year, 4, 1);
        log.debug("Financial year start: {} -> {}", year, start);
        return start;
    }

    /**
     * @author Arpit Chaurasia
     */
    public static LocalDate getFinancialYearEnd(int year) {
        LocalDate end = LocalDate.of(year + 1, 3, 31);
        log.debug("Financial year end: {} -> {}", year, end);
        return end;
    }

    /**
     * @author Arpit Chaurasia
     */
    public static int getCurrentFinancialYear() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        int fy = (month >= 4) ? year : year - 1;

        log.debug("Current financial year calculated: {}", fy);
        return fy;
    }
}
