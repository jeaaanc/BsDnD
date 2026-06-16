package BankSdNd.example.BsDnD.core.domain.exception;

public class DuplicateException extends BusinessException {
    public DuplicateException(String messageKey) {
        super(messageKey);
    }

    public DuplicateException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
