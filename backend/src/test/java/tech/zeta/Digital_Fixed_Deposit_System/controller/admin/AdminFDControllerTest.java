package tech.zeta.Digital_Fixed_Deposit_System.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.*;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositService;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void getAdminFinancialYearSummary_returnsOk() {
        AdminFDController controller = new AdminFDController(new FixedDepositServiceStub());

        ResponseEntity<FDFinancialYearSummaryResponse> response = controller.getAdminFinancialYearSummary(2025);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getFDsByFinancialYear_returnsOk() {
        AdminFDController controller = new AdminFDController(new FixedDepositServiceStub());

        ResponseEntity<List<FixedDeposit>> response = controller.getFDsByFinancialYear(2025);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getAllFDsChronological_returnsOk() {
        AdminFDController controller = new AdminFDController(new FixedDepositServiceStub());

        ResponseEntity<List<FixedDeposit>> response = controller.getAllFDsChronological("asc");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getUserPortfolioAsAdmin_returnsOk() {
        AdminFDController controller = new AdminFDController(new FixedDepositServiceStub());

        ResponseEntity<FDPortfolioResponse> response = controller.getUserPortfolioAsAdmin(10L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getInterestTimelineAsAdmin_withDefaultInterval() {
        AdminFDController controller = new AdminFDController(new FixedDepositServiceStub());

        ResponseEntity<FDInterestTimelineResponse> response = controller.getInterestTimelineAsAdmin(1L, null);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    private static class FixedDepositServiceStub extends FixedDepositService {
        FixedDepositServiceStub() {
            super(null, null, null);
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

        @Override
        public FDFinancialYearSummaryResponse getAdminFinancialYearSummary(Integer year) {
            return new FDFinancialYearSummaryResponse("2024-2025", 1, new BigDecimal("5000"), new BigDecimal("100"));
        }

        @Override
        public List<FixedDeposit> getAdminFDsByFinancialYear(Integer year) {
            FixedDeposit fd = new FixedDeposit();
            fd.setUserId(10L);
            return List.of(fd);
        }

        @Override
        public List<FixedDeposit> getAllFDsChronological(String order) {
            FixedDeposit fd = new FixedDeposit();
            fd.setUserId(10L);
            return List.of(fd);
        }

        @Override
        public FDPortfolioResponse getUserFDPortfolio(Long userId) {
            return new FDPortfolioResponse(1, 1, 0, 0, new BigDecimal("5000"), new BigDecimal("100"), LocalDate.now().plusDays(10));
        }

        @Override
        public FixedDeposit getFixedDepositByIdForAdmin(Long fdId) {
            FixedDeposit fd = new FixedDeposit();
            fd.setInterestScheme(InterestScheme.STANDARD_6_MONTHS);
            fd.setStartDate(LocalDate.now().minusDays(10));
            fd.setMaturityDate(LocalDate.now().plusDays(10));
            fd.setInterestRate(new BigDecimal("5.50"));
            fd.setAmount(new BigDecimal("5000"));
            return fd;
        }

        @Override
        public FDInterestTimelineResponse getInterestTimeline(FixedDeposit fd, String interval) {
            return new FDInterestTimelineResponse(
                    1L,
                    new BigDecimal("5000"),
                    new BigDecimal("5.50"),
                    interval,
                    List.of()
            );
        }
    }

    private void setField(Object target, String field, Object value) throws Exception {
        Field declaredField = target.getClass().getDeclaredField(field);
        declaredField.setAccessible(true);
        declaredField.set(target, value);
    }
}
