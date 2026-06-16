package BankSdNd.example.BsDnD.core.domain.exception;

public class ValidationException extends BusinessException {
    public ValidationException(String messageKey) {
        super(messageKey);
    }

    public ValidationException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
