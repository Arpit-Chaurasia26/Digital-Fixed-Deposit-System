package tech.zeta.Digital_Fixed_Deposit_System.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionTest {

    @Test
    void businessException_setsMessage() {
        BusinessException ex = new BusinessException("business");
        assertEquals("business", ex.getMessage());
    }

    @Test
    void accountNotFoundException_setsMessage() {
        AccountNotFoundException ex = new AccountNotFoundException("missing");
        assertEquals("missing", ex.getMessage());
    }

    @Test
    void unauthorizedException_setsMessage() {
        UnauthorizedException ex = new UnauthorizedException("nope");
        assertEquals("nope", ex.getMessage());
    }

    @Test
    void resourceNotFoundException_setsMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("resource");
        assertEquals("resource", ex.getMessage());
    }

    @Test
    void invalidOperationException_hasNullMessage() {
        InvalidOperationException ex = new InvalidOperationException("ignored");
        assertNull(ex.getMessage());
    }
}
