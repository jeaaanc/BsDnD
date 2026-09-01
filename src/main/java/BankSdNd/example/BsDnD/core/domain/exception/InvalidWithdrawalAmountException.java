package BankSdNd.example.BsDnD.core.domain.exception;

public class InvalidWithdrawalAmountException extends BusinessException {
    public InvalidWithdrawalAmountException(String message) {
        super(message);
    }

    public InvalidWithdrawalAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
