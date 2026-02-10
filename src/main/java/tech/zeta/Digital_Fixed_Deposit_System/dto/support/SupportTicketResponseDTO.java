package tech.zeta.Digital_Fixed_Deposit_System.dto.support;

import lombok.Getter;
import lombok.Setter;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;

@Setter
@Getter
public class SupportTicketResponseDTO {

    private Long id;
    private Long userId;
    private Long fdId;
    private String subject;
    private String description;
    private TicketStatus status;
    private String response;

    public SupportTicketResponseDTO() {}

}
