package BankSdNd.example.BsDnD.core.domain.exception;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String messageKey) {
        super(messageKey);
    }

    public AccountNotFoundException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
