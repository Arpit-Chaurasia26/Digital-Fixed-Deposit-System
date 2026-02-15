package tech.zeta.Digital_Fixed_Deposit_System.exception;

/**
 * @author Akshaya Siripuram
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }
}
