package tech.zeta.Digital_Fixed_Deposit_System.exception;

public class GlobalExceptionHandler extends RuntimeException{
    public GlobalExceptionHandler(String message){
        super(message);
    }
}
