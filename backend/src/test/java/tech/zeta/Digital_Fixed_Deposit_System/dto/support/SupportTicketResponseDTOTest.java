package tech.zeta.Digital_Fixed_Deposit_System.dto.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SupportTicketResponseDTO Tests")
class SupportTicketResponseDTOTest {

    @Test
    void gettersAndSetters_work() {
        SupportTicketResponseDTO dto = new SupportTicketResponseDTO();
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        LocalDateTime updated = LocalDateTime.now();

        dto.setId(1L);
        dto.setUserId(2L);
        dto.setFdId(3L);
        dto.setSubject("subject");
        dto.setDescription("desc");
        dto.setStatus(TicketStatus.RESOLVED);
        dto.setResponse("response");
        dto.setCreatedTime(created);
        dto.setUpdatedTime(updated);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUserId()).isEqualTo(2L);
        assertThat(dto.getFdId()).isEqualTo(3L);
        assertThat(dto.getSubject()).isEqualTo("subject");
        assertThat(dto.getDescription()).isEqualTo("desc");
        assertThat(dto.getStatus()).isEqualTo(TicketStatus.RESOLVED);
        assertThat(dto.getResponse()).isEqualTo("response");
        assertThat(dto.getCreatedTime()).isEqualTo(created);
        assertThat(dto.getUpdatedTime()).isEqualTo(updated);
    }
}

