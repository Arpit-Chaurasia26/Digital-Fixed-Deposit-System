
package tech.zeta.Digital_Fixed_Deposit_System.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
