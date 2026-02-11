package tech.zeta.Digital_Fixed_Deposit_System.dto.common;

import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApiResponse {

    private static final Logger logger = LogManager.getLogger(ApiResponse.class);

    private final String message;
    private final int status;
    private final Instant timestamp;

    public ApiResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now();
        logger.debug("ApiResponse created with status={}", status);
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
