package BankSdNd.example.BsDnD.core.domain.exception;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String messageKey) {
        super(messageKey);
    }

    public UserNotFoundException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
