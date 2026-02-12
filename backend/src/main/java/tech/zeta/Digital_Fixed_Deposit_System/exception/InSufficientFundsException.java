package tech.zeta.Digital_Fixed_Deposit_System.exception;

public class InSufficientFundsException extends RuntimeException {
    public InSufficientFundsException(String message) {
        super(message);
    }
}
