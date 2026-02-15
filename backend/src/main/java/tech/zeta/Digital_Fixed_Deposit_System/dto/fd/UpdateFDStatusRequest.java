package tech.zeta.Digital_Fixed_Deposit_System.dto.fd;

import jakarta.validation.constraints.NotNull;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FDStatus;

/**
 * @author Arpit Chaurasia
 */
public class UpdateFDStatusRequest {

    @NotNull(message = "FD status must be provided")
    private FDStatus status;

    public FDStatus getStatus() {
        return status;
    }
}
