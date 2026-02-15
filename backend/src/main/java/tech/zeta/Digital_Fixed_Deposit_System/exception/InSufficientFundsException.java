package tech.zeta.Digital_Fixed_Deposit_System.exception;

/**
 * @author Pavan Kalloji
 */
public class InSufficientFundsException extends RuntimeException {
  public InSufficientFundsException(String message) {
    super(message);
  }
}
