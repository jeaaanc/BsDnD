package BankSdNd.example.BsDnD.core.domain.exception;

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException(String messageKey) {
        super(messageKey);
    }

    public InvalidPasswordException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
