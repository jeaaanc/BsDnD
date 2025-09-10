package BankSdNd.example.BsDnD.exception.business;

public class InsufficientBalanceException extends BusinessException{
    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
