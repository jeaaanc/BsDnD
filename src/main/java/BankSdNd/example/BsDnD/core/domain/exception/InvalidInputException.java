package BankSdNd.example.BsDnD.core.domain.exception;

public class InvalidInputException extends BusinessException {
    public InvalidInputException(String messageKey) {
        super(messageKey);
    }

    public InvalidInputException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
