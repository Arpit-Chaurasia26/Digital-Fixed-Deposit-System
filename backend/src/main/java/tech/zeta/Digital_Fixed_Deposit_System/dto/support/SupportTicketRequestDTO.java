package tech.zeta.Digital_Fixed_Deposit_System.dto.support;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;

@Setter
@Getter
public class SupportTicketRequestDTO {

    private Long userId;
    private Long fdId;
    @NotBlank
    @Size(max = 200)
    private String subject;
    @NotBlank
    @Size(max = 1000)
    private String description;
    private TicketStatus status;
    private String response;

    public SupportTicketRequestDTO() {}

}
