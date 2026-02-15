package tech.zeta.Digital_Fixed_Deposit_System.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.zeta.Digital_Fixed_Deposit_System.dto.common.ApiResponse;

// Author - Arpit Chaurasia
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @author Priyanshu Mishra
     */


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> handleBusinessException(
            BusinessException ex
    ) {
        log.warn("Business exception: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    /**
     * @author Priyanshu Mishra
     */


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedException(
            UnauthorizedException ex
    ) {
        log.warn("Unauthorized exception: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.UNAUTHORIZED.value()
                ));
    }

    // Author - Arpit Chaurasia
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex
    ) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    // Author - Arpit Chaurasia
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        log.warn("Validation exception: {}", message);
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(message, 400));
    }

    //AccountNotFound
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponse> handleAccountNotFoundException(
            AccountNotFoundException ex
    ) {
        log.warn("Account not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiResponse> handleInvalidOperationException(
            InvalidOperationException ex
    ) {
        log.warn("Invalid operation: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    @ExceptionHandler(InSufficientFundsException.class)
    public ResponseEntity<ApiResponse> handleInSufficientFundsException(
            InSufficientFundsException ex
    ) {
        log.warn("Insufficient funds: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    /**
     * @author Priyanshu Mishra
     */


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse> handleValidationException(
            ValidationException ex
    ) {
        log.warn("Validation exception: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    /**
     * @author Priyanshu Mishra
     */


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex
    ) {
        log.warn("Missing request parameter: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }


    /**
     * @author Priyanshu Mishra
     */
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse> handleAccountLockedException(
            AccountLockedException ex
    ) {
        long minutes = ex.getRemainingSeconds() / 60;
        long seconds = ex.getRemainingSeconds() % 60;

        String message = String.format(
                "Too many attempts. Try again in %d min %d sec",
                minutes,
                seconds
        );

        log.warn("Account locked: {}", message);
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ApiResponse(message, 429));
    }


/*
    Author - Akshaya Siripuram
 */
    // ===== Support Ticket Module Exceptions =====


    // Ticket not found (404)
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ApiResponse> handleTicketNotFoundException(
            TicketNotFoundException ex
    ) {
        log.warn("Ticket not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }

    // Unauthorized ticket actions (403)
    @ExceptionHandler(UnauthorizedTicketActionException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedTicketActionException(
            UnauthorizedTicketActionException ex
    ) {
        log.warn("Unauthorized ticket action: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.FORBIDDEN.value()
                ));
    }

    // Invalid ticket status transitions (400)
    @ExceptionHandler(InvalidTicketStatusException.class)
    public ResponseEntity<ApiResponse> handleInvalidTicketStatusException(
            InvalidTicketStatusException ex
    ) {
        log.warn("Invalid ticket status: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    // User not found (404)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(
            UserNotFoundException ex
    ) {
        log.warn("User not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                ));
    }


    // Author - Arpit Chaurasia
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(
            Exception ex
    ) {
        log.error("Unhandled exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(
                        "Internal server error",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }

}

