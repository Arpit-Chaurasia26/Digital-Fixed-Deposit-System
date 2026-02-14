package tech.zeta.Digital_Fixed_Deposit_System.exception;

import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.zeta.Digital_Fixed_Deposit_System.dto.common.ApiResponse;

// Author - Arpit Chaurasia
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Author - Arpit Chaurasia
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> handleBusinessException(
            BusinessException ex
    ) {
        logger.warn("Business exception: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

    // Author - Arpit Chaurasia
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedException(
            UnauthorizedException ex
    ) {
        logger.warn("Unauthorized exception: {}", ex.getMessage());
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
        logger.warn("Resource not found: {}", ex.getMessage());
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

        logger.warn("Validation exception: {}", message);
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(message, 400));
    }

    //AccountNotFound
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponse> handleAccountNotFoundException(
            AccountNotFoundException ex
    ) {
        logger.warn("Account not found: {}", ex.getMessage());
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
        logger.warn("Invalid operation: {}", ex.getMessage());
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
        logger.warn("Insufficient funds: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }
/*
Author : Priyanshu Mishra
*/

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse> handleValidationException(
            ValidationException ex
    ) {
        logger.warn("Validation exception: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }

/*
Author : Priyanshu Mishra
*/

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex
    ) {
        logger.warn("Missing request parameter: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()
                ));
    }


    // Author - Arpit Chaurasia
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(
            Exception ex
    ) {
        logger.error("Unhandled exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(
                        "Internal server error",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }

/*
Author : Priyanshu Mishra
*/

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse> handleAccountLocked(
            AccountLockedException ex
    ) {
        long minutes = ex.getRemainingSeconds() / 60;
        long seconds = ex.getRemainingSeconds() % 60;

        String message = String.format(
                "Too many attempts. Try again in %d min %d sec",
                minutes,
                seconds
        );

        logger.warn("Account locked: {}", message);
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ApiResponse(message, 429));
    }

}
