package tech.zeta.Digital_Fixed_Deposit_System.exception;

/**
 * @author Arpit Chaurasia
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
