package tech.zeta.Digital_Fixed_Deposit_System.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketRequestDTO;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketResponseDTO;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.SupportTicket;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SupportTicketMapper Tests")
class SupportTicketMapperTest {

    private final SupportTicketMapper mapper = new SupportTicketMapper();

    @Test
    void toEntity_mapsFields() {
        SupportTicketRequestDTO dto = new SupportTicketRequestDTO();
        dto.setUserId(1L);
        dto.setFdId(2L);
        dto.setSubject("subject");
        dto.setDescription("desc");
        dto.setStatus(TicketStatus.OPEN);
        dto.setResponse("response");

        SupportTicket entity = mapper.toEntity(dto);

        assertThat(entity.getUserId()).isEqualTo(1L);
        assertThat(entity.getFdId()).isEqualTo(2L);
        assertThat(entity.getSubject()).isEqualTo("subject");
        assertThat(entity.getDescription()).isEqualTo("desc");
        assertThat(entity.getStatus()).isEqualTo(TicketStatus.OPEN);
        assertThat(entity.getResponse()).isEqualTo("response");
    }

    @Test
    void toResponseDTO_mapsFields() {
        SupportTicket entity = new SupportTicket();
        entity.setId(10L);
        entity.setUserId(11L);
        entity.setFdId(12L);
        entity.setSubject("subject");
        entity.setDescription("desc");
        entity.setStatus(TicketStatus.RESOLVED);
        entity.setResponse("response");
        entity.setCreatedTime(LocalDateTime.now().minusDays(1));
        entity.setUpdatedTime(LocalDateTime.now());

        SupportTicketResponseDTO dto = mapper.toResponseDTO(entity);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getUserId()).isEqualTo(11L);
        assertThat(dto.getFdId()).isEqualTo(12L);
        assertThat(dto.getSubject()).isEqualTo("subject");
        assertThat(dto.getDescription()).isEqualTo("desc");
        assertThat(dto.getStatus()).isEqualTo(TicketStatus.RESOLVED);
        assertThat(dto.getResponse()).isEqualTo("response");
        assertThat(dto.getCreatedTime()).isEqualTo(entity.getCreatedTime());
        assertThat(dto.getUpdatedTime()).isEqualTo(entity.getUpdatedTime());
    }
}

