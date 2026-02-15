package tech.zeta.Digital_Fixed_Deposit_System.exception;

/**
 * @author Arpit Chaurasia
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
