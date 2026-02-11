package tech.zeta.Digital_Fixed_Deposit_System.util;

import java.time.LocalDate;

public final class DateUtils {

    private DateUtils() {}

    public static LocalDate getFinancialYearStart(int year) {
        return LocalDate.of(year, 4, 1);
    }

    public static LocalDate getFinancialYearEnd(int year) {
        return LocalDate.of(year + 1, 3, 31);
    }

    public static int getCurrentFinancialYear() {
        LocalDate today = LocalDate.now();
        return today.getMonthValue() >= 4
                ? today.getYear()
                : today.getYear() - 1;
    }
}
