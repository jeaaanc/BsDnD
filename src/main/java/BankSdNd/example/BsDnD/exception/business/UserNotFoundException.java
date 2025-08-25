package BankSdNd.example.BsDnD.exception.business;

public class UserNotFoundException extends BusinessException{
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
