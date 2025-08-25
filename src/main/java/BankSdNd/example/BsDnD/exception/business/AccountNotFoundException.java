package BankSdNd.example.BsDnD.exception.business;

public class AccountNotFoundException extends BusinessException{
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
