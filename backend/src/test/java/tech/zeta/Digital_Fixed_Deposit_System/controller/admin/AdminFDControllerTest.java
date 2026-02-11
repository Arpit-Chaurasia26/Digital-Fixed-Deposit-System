package tech.zeta.Digital_Fixed_Deposit_System.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.FDMaturityResponse;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.UpdateFDStatusRequest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositService;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdminFDControllerTest {

    @Test
    void updateFDStatus_returnsOk() throws Exception {
        AdminFDController controller = new AdminFDController(new FixedDepositServiceStub());
        UpdateFDStatusRequest request = new UpdateFDStatusRequest();
        setField(request, "status", FDStatus.MATURED);

        ResponseEntity<FixedDeposit> response = controller.updateFDStatus(1L, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(FDStatus.MATURED, response.getBody().getStatus());
    }

    @Test
    void getAllMaturingFDs_returnsList() {
        AdminFDController controller = new AdminFDController(new FixedDepositServiceStub());

        ResponseEntity<List<FDMaturityResponse>> response = controller.getAllMaturingFDs(7);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getFdId());
    }

    private static class FixedDepositServiceStub extends FixedDepositService {
        FixedDepositServiceStub() {
            super(null, null);
        }

        @Override
        public FixedDeposit updateFixedDepositStatus(Long fdId, FDStatus newStatus) {
            FixedDeposit fd = new FixedDeposit();
            fd.setStatus(newStatus);
            return fd;
        }

        @Override
        public List<FDMaturityResponse> getAllFDsMaturingWithinDays(int days) {
            return List.of(new FDMaturityResponse(
                    1L,
                    10L,
                    new BigDecimal("1000"),
                    LocalDate.now().plusDays(2),
                    2,
                    FDStatus.ACTIVE
            ));
        }
    }

    private void setField(Object target, String field, Object value) throws Exception {
        Field declaredField = target.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);
        declaredField.set(target, value);
    }
}
