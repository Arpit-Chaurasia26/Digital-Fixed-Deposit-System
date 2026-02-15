package tech.zeta.Digital_Fixed_Deposit_System.exception;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import tech.zeta.Digital_Fixed_Deposit_System.dto.common.ApiResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Priyanshu Mishra
 */

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

    @Test
    void handleMethodArgumentNotValid_returnsBadRequestWithFieldMessage() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "req");
        bindingResult.addError(new FieldError("req", "field", "invalid"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiResponse> response = handler.handleValidationException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("invalid", response.getBody().getMessage());
    }

    @Test
    void handleInSufficientFundsException_returnsBadRequest() {
        ResponseEntity<ApiResponse> response =
                handler.handleInSufficientFundsException(new InSufficientFundsException("low"));

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("low", response.getBody().getMessage());
    }

    @Test
    void handleValidationException_returnsBadRequest() {
        ResponseEntity<ApiResponse> response =
                handler.handleValidationException(new ValidationException("invalid"));

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("invalid", response.getBody().getMessage());
    }
    /**
     * @author Priyanshu Mishra
     */

    @Test
    void handleMissingServletRequestParameterException_returnsBadRequest() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("missing", "String");

        ResponseEntity<ApiResponse> response =
                handler.handleMissingServletRequestParameterException(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(exception.getMessage(), response.getBody().getMessage());
    }

    /**
     * @author Priyanshu Mishra
     */
    @Test
    void handleAccountLocked_returnsTooManyRequests() {
        ResponseEntity<ApiResponse> response = handler.handleAccountLockedException(
                new AccountLockedException("locked", 125L)
        );

        assertEquals(429, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Too many attempts. Try again in 2 min 5 sec", response.getBody().getMessage());
    }

    @Test
    void handleInvalidTicketStatusException_returnsBadRequest() {
        ResponseEntity<ApiResponse> response =
                handler.handleInvalidTicketStatusException(new InvalidTicketStatusException("bad-status"));

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("bad-status", response.getBody().getMessage());
    }

    @Test
    void handleUnauthorizedTicketActionException_returnsForbidden() {
        ResponseEntity<ApiResponse> response =
                handler.handleUnauthorizedTicketActionException(new UnauthorizedTicketActionException("denied"));

        assertEquals(403, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("denied", response.getBody().getMessage());
    }

    @Test
    void handleTicketNotFoundException_returnsNotFound() {
        ResponseEntity<ApiResponse> response =
                handler.handleTicketNotFoundException(new TicketNotFoundException(12L));

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Support ticket not found with id: 12", response.getBody().getMessage());
    }

    @Test
    void handleUserNotFoundException_returnsNotFound() {
        ResponseEntity<ApiResponse> response =
                handler.handleUserNotFoundException(new UserNotFoundException(12L));

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("User not found with id: 12", response.getBody().getMessage());
    }

    @Test
    void handleMethodArgumentNotValid_usesFirstFieldError_whenMultipleErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "req");
        bindingResult.addError(new FieldError("req", "field", "first"));
        bindingResult.addError(new FieldError("req", "other", "second"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiResponse> response = handler.handleValidationException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("first", response.getBody().getMessage());
    }

    @Test
    void handleAccountLocked_formatsZeroSeconds() {
        ResponseEntity<ApiResponse> response = handler.handleAccountLockedException(
                new AccountLockedException("locked", 60L)
        );

        assertEquals(429, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Too many attempts. Try again in 1 min 0 sec", response.getBody().getMessage());
    }
}
