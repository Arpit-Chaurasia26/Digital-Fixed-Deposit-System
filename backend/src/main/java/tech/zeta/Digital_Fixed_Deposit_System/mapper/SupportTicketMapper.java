package tech.zeta.Digital_Fixed_Deposit_System.mapper;
import org.springframework.stereotype.Component;

import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketRequestDTO;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketResponseDTO;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.SupportTicket;

@Component
public class SupportTicketMapper {

    // Request → Entity
    public SupportTicket toEntity(SupportTicketRequestDTO dto) {
        SupportTicket ticket = new SupportTicket();
        ticket.setUserId(dto.getUserId());
        ticket.setFdId(dto.getFdId());
        ticket.setSubject(dto.getSubject());
        ticket.setDescription(dto.getDescription());
        ticket.setStatus(dto.getStatus());
        ticket.setResponse(dto.getResponse());
        return ticket;
    }

    // Entity → Response
    public SupportTicketResponseDTO toResponseDTO(SupportTicket ticket) {
        SupportTicketResponseDTO dto = new SupportTicketResponseDTO();
        dto.setId(ticket.getId());
        dto.setUserId(ticket.getUserId());
        dto.setFdId(ticket.getFdId());
        dto.setSubject(ticket.getSubject());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus());
        dto.setResponse(ticket.getResponse());
        return dto;
    }
}
