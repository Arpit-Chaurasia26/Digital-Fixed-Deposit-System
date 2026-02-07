package tech.zeta.Digital_Fixed_Deposit_System.dto.common;

import java.time.Instant;

public class ApiResponse {

    private final String message;
    private final int status;
    private final Instant timestamp;

    public ApiResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now();
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
