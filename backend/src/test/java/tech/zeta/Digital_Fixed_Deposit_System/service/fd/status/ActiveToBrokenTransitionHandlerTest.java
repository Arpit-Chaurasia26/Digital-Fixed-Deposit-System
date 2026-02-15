package tech.zeta.Digital_Fixed_Deposit_System.service.fd.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDeposit;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.InterestScheme;
import tech.zeta.Digital_Fixed_Deposit_System.exception.BusinessException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Arpit Chaurasia
 */
@DisplayName("ActiveToBrokenTransitionHandler Unit Tests")
class ActiveToBrokenTransitionHandlerTest {

    private final ActiveToBrokenTransitionHandler handler = new ActiveToBrokenTransitionHandler();

    @Test
    @DisplayName("Supports ACTIVE -> BROKEN")
    void supportsActiveToBroken() {
        assertThat(handler.supports(FDStatus.ACTIVE, FDStatus.BROKEN)).isTrue();
        assertThat(handler.supports(FDStatus.MATURED, FDStatus.BROKEN)).isFalse();
    }

    @Test
    @DisplayName("Applies transition when premature break is allowed")
    void apply_WhenAllowed_ShouldSetBroken() {
        FixedDeposit fd = new FixedDeposit();
        fd.setStatus(FDStatus.ACTIVE);
        fd.setInterestScheme(InterestScheme.STANDARD_12_MONTHS); // allowed
        fd.setMaturityDate(LocalDate.now().plusDays(5));

        handler.apply(fd);

        assertThat(fd.getStatus()).isEqualTo(FDStatus.BROKEN);
    }

    @Test
    @DisplayName("Throws when premature break not allowed")
    void apply_WhenNotAllowed_ShouldThrow() {
        FixedDeposit fd = new FixedDeposit();
        fd.setStatus(FDStatus.ACTIVE);
        fd.setInterestScheme(InterestScheme.TAX_SAVER_5_YEARS); // not allowed
        fd.setMaturityDate(LocalDate.now().plusDays(5));

        assertThatThrownBy(() -> handler.apply(fd))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Premature withdrawal");
    }

    @Test
    @DisplayName("Throws when FD already matured")
    void apply_WhenAlreadyMatured_ShouldThrow() {
        FixedDeposit fd = new FixedDeposit();
        fd.setStatus(FDStatus.ACTIVE);
        fd.setInterestScheme(InterestScheme.STANDARD_12_MONTHS);
        fd.setMaturityDate(LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> handler.apply(fd))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already matured");
    }
}

