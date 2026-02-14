/*
Author : Priyanshu Mishra
*/
package tech.zeta.Digital_Fixed_Deposit_System.exception;

public class AccountLockedException extends RuntimeException {

    private final long remainingSeconds;

    public AccountLockedException(String message, long remainingSeconds) {
        super(message);
        this.remainingSeconds = remainingSeconds;
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }
}
