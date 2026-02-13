package tech.zeta.Digital_Fixed_Deposit_System.exception;

// Author - Arpit Chaurasia
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
