package BankSdNd.example.BsDnD.core.domain.exception;

public class InvalidDepositAmountException extends BusinessException {
    public InvalidDepositAmountException(String message) {
        super(message);
    }

    public InvalidDepositAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
