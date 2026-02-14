package tech.zeta.Digital_Fixed_Deposit_System.dto.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SupportTicketRequestDTO Tests")
class SupportTicketRequestDTOTest {

    @Test
    void gettersAndSetters_work() {
        SupportTicketRequestDTO dto = new SupportTicketRequestDTO();
        dto.setUserId(10L);
        dto.setFdId(20L);
        dto.setSubject("subject");
        dto.setDescription("desc");
        dto.setStatus(TicketStatus.OPEN);
        dto.setResponse("response");

        assertThat(dto.getUserId()).isEqualTo(10L);
        assertThat(dto.getFdId()).isEqualTo(20L);
        assertThat(dto.getSubject()).isEqualTo("subject");
        assertThat(dto.getDescription()).isEqualTo("desc");
        assertThat(dto.getStatus()).isEqualTo(TicketStatus.OPEN);
        assertThat(dto.getResponse()).isEqualTo("response");
    }
}

