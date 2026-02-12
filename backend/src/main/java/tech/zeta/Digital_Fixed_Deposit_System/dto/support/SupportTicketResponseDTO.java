package tech.zeta.Digital_Fixed_Deposit_System.dto.support;

import lombok.Getter;
import lombok.Setter;
import tech.zeta.Digital_Fixed_Deposit_System.entity.support.TicketStatus;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public SupportTicketResponseDTO() {}
}