package tech.zeta.Digital_Fixed_Deposit_System.util;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    private DateUtils() {}

    public static LocalDate getFinancialYearStart(int year) {
        LocalDate start = LocalDate.of(year, 4, 1);
        logger.debug("Financial year start: {} -> {}", year, start);
        return start;
    }

    public static LocalDate getFinancialYearEnd(int year) {
        LocalDate end = LocalDate.of(year + 1, 3, 31);
        logger.debug("Financial year end: {} -> {}", year, end);
        return end;
    }

    public static int getCurrentFinancialYear() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        int fy = (month >= 4) ? year : year - 1;

        logger.debug("Current financial year calculated: {}", fy);
        return fy;
    }
}
