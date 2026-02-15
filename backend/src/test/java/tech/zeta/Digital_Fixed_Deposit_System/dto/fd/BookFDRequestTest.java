package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arpit Chaurasia
 */
@DisplayName("BookFDRequest DTO Tests")
class BookFDRequestTest {

    @Test
    void getters_returnInjectedValues() throws Exception {
        BookFDRequest dto = new BookFDRequest();
        setField(dto, "amount", new BigDecimal("5000.00"));
        setField(dto, "interestScheme", InterestScheme.STANDARD_6_MONTHS);

        assertThat(dto.getAmount()).isEqualByComparingTo("5000.00");
        assertThat(dto.getInterestScheme()).isEqualTo(InterestScheme.STANDARD_6_MONTHS);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}

