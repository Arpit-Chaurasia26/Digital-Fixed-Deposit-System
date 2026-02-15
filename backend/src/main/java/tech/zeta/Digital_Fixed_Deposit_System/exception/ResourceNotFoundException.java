package tech.zeta.Digital_Fixed_Deposit_System.exception;

/**
 * @author Arpit Chaurasia
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
