package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arpit Chaurasia
 */
@DisplayName("UpdateFDStatusRequest DTO Tests")
class UpdateFDStatusRequestTest {

    @Test
    void getter_returnsInjectedStatus() throws Exception {
        UpdateFDStatusRequest dto = new UpdateFDStatusRequest();
        setField(dto, "status", FDStatus.MATURED);

        assertThat(dto.getStatus()).isEqualTo(FDStatus.MATURED);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}

