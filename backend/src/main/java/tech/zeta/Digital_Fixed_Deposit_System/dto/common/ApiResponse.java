package tech.zeta.Digital_Fixed_Deposit_System.dto.common;

import java.time.Instant;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Priyanshu Mishra
 */


@Slf4j
public class ApiResponse {


    private final String message;
    private final int status;
    private final Instant timestamp;

    public ApiResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now();
        log.debug("ApiResponse created with status={}", status);
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
