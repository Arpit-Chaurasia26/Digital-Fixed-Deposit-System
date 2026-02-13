package tech.zeta.Digital_Fixed_Deposit_System.exception;

// Author - Arpit Chaurasia
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
