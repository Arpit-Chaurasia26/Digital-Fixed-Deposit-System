package tech.zeta.Digital_Fixed_Deposit_System.entity.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TicketStatus Enum Tests")
class TicketStatusTest {

    @Test
    void values_areStable() {
        assertThat(TicketStatus.values()).containsExactly(
                TicketStatus.OPEN,
                TicketStatus.RESOLVED,
                TicketStatus.CLOSED
        );
    }
}

