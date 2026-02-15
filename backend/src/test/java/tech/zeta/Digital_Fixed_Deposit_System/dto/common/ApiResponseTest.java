package tech.zeta.Digital_Fixed_Deposit_System.dto.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Priyanshu Mishra
 */

public class ApiResponseTest {

    @Test
    void createsResponseWithMessageStatusAndTimestamp() {
        ApiResponse response = new ApiResponse("ok", 200);

        assertEquals("ok", response.getMessage());
        assertEquals(200, response.getStatus());
        assertNotNull(response.getTimestamp());
    }
}
