package tech.zeta.Digital_Fixed_Deposit_System.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import tech.zeta.Digital_Fixed_Deposit_System.dto.common.ApiResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBusinessException_returnsBadRequest() {
        ResponseEntity<ApiResponse> response =
                handler.handleBusinessException(new BusinessException("bad"));

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("bad", response.getBody().getMessage());
    }

    @Test
    void handleUnauthorizedException_returnsUnauthorized() {
        ResponseEntity<ApiResponse> response =
                handler.handleUnauthorizedException(new UnauthorizedException("unauth"));

        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("unauth", response.getBody().getMessage());
    }

    @Test
    void handleResourceNotFoundException_returnsNotFound() {
        ResponseEntity<ApiResponse> response =
                handler.handleResourceNotFoundException(new ResourceNotFoundException("missing"));

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("missing", response.getBody().getMessage());
    }

    @Test
    void handleAccountNotFoundException_returnsNotFound() {
        ResponseEntity<ApiResponse> response =
                handler.handleAccountNotFoundException(new AccountNotFoundException("missing"));

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("missing", response.getBody().getMessage());
    }

    @Test
    void handleInvalidOperationException_returnsBadRequest() {
        ResponseEntity<ApiResponse> response =
                handler.handleInvalidOperationException(new InvalidOperationException("invalid"));

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("invalid", response.getBody().getMessage());
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        ResponseEntity<ApiResponse> response =
                handler.handleGenericException(new RuntimeException("boom"));

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().getMessage());
    }
}
