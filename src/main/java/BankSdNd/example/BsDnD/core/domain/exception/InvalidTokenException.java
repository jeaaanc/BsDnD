package BankSdNd.example.BsDnD.core.domain.exception;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException(String messageKey) {
        super(messageKey);
    }

    public InvalidTokenException(String messageKey, Throwable cause) {
        super(messageKey);
        if (cause != null) {
            this.initCause(cause);
        }
    }
}
