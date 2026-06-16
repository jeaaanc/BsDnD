package BankSdNd.example.BsDnD.core.domain.exception;

public class TokenGenerationException extends BusinessException {
    public TokenGenerationException(String messageKey, Throwable cause) {
        super(messageKey);
        if (cause != null) {
            this.initCause(cause);
        }
    }
}
