package BankSdNd.example.BsDnD.core.domain.exception;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
