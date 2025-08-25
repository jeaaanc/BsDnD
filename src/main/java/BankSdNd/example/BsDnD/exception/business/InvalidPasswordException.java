package BankSdNd.example.BsDnD.exception.business;

public class InvalidPasswordException extends BusinessException{
    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
