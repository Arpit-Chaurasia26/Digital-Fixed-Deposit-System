package tech.zeta.Digital_Fixed_Deposit_System.service.fd.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Arpit Chaurasia
 */
@DisplayName("ActiveToMaturedTransitionHandler Unit Tests")
class ActiveToMaturedTransitionHandlerTest {

    private final ActiveToMaturedTransitionHandler handler = new ActiveToMaturedTransitionHandler();

    @Test
    @DisplayName("Supports ACTIVE -> MATURED")
    void supportsActiveToMatured() {
        assertThat(handler.supports(FDStatus.ACTIVE, FDStatus.MATURED)).isTrue();
        assertThat(handler.supports(FDStatus.ACTIVE, FDStatus.CLOSED)).isFalse();
    }

    @Test
    @DisplayName("Applies transition when maturity date reached")
    void apply_WhenMatured_ShouldSetMatured() {
        FixedDeposit fd = new FixedDeposit();
        fd.setStatus(FDStatus.ACTIVE);
        fd.setMaturityDate(LocalDate.now().minusDays(1));

        handler.apply(fd);

        assertThat(fd.getStatus()).isEqualTo(FDStatus.MATURED);
    }

    @Test
    @DisplayName("Throws when maturity date not reached")
    void apply_WhenNotMatured_ShouldThrow() {
        FixedDeposit fd = new FixedDeposit();
        fd.setStatus(FDStatus.ACTIVE);
        fd.setMaturityDate(LocalDate.now().plusDays(5));

        assertThatThrownBy(() -> handler.apply(fd))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not yet reached maturity");
    }
}

