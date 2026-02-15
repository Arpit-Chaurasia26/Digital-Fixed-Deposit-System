package tech.zeta.Digital_Fixed_Deposit_System.controller.withdrawal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalEligibility;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalHistory;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalPreview;
import tech.zeta.Digital_Fixed_Deposit_System.dto.withdrawal.WithdrawalReceipt;
import tech.zeta.Digital_Fixed_Deposit_System.service.withdrawal.WithdrawalService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@DisplayName("WithdrawalController Unit Tests")
class WithdrawalControllerTest {

    @Test
    @DisplayName("getWithdrawalPreview returns response")
    void getWithdrawalPreview_returnsResponse() {
        WithdrawalService service = Mockito.mock(WithdrawalService.class);
        WithdrawalController controller = new WithdrawalController();
        controller.setWithdrawalService(service);

        WithdrawalPreview preview = new WithdrawalPreview(
                BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.TEN
        );
        when(service.getWithdrawalPreview(eq(1L), any())).thenReturn(preview);

        ResponseEntity<WithdrawalPreview> response = controller.getWithdrawalPreview(1L, BigDecimal.TEN);

        assertThat(response.getBody()).isEqualTo(preview);
    }

    @Test
    @DisplayName("confirmWithdrawal returns receipt")
    void confirmWithdrawal_returnsReceipt() {
        WithdrawalService service = Mockito.mock(WithdrawalService.class);
        WithdrawalController controller = new WithdrawalController();
        controller.setWithdrawalService(service);

        WithdrawalReceipt receipt = new WithdrawalReceipt(1L, BigDecimal.TEN, BigDecimal.ONE,
                BigDecimal.ONE, null, null, null);
        when(service.confirmWithdrawal(eq(1L), any())).thenReturn(receipt);

        ResponseEntity<WithdrawalReceipt> response = controller.confirmWithdrawal(1L, BigDecimal.TEN);

        assertThat(response.getBody()).isEqualTo(receipt);
    }

    @Test
    @DisplayName("checkWithdrawalEligibility returns eligibility")
    void checkWithdrawalEligibility_returnsEligibility() {
        WithdrawalService service = Mockito.mock(WithdrawalService.class);
        WithdrawalController controller = new WithdrawalController();
        controller.setWithdrawalService(service);

        WithdrawalEligibility eligibility = new WithdrawalEligibility(true, "ok");
        when(service.checkWithdrawalEligibility(1L)).thenReturn(eligibility);

        ResponseEntity<WithdrawalEligibility> response = controller.checkWithdrawalEligibility(1L);

        assertThat(response.getBody()).isEqualTo(eligibility);
    }

    @Test
    @DisplayName("getWithdrawalsHistory returns list")
    void getWithdrawalsHistory_returnsList() {
        WithdrawalService service = Mockito.mock(WithdrawalService.class);
        WithdrawalController controller = new WithdrawalController();
        controller.setWithdrawalService(service);

        when(service.getWithdrawalHistory(5L)).thenReturn(List.of(new WithdrawalHistory(1L, BigDecimal.TEN, BigDecimal.ONE, null)));

        ResponseEntity<List<WithdrawalHistory>> response = controller.getWithdrawalsHistory(5L);

        assertThat(response.getBody()).hasSize(1);
    }
}

