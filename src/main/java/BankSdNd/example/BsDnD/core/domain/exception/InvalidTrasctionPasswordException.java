package BankSdNd.example.BsDnD.core.domain.exception;

public class InvalidTrasctionPasswordException extends BusinessException {

    public InvalidTrasctionPasswordException(String message) {
        super(message);
    }

    public InvalidTrasctionPasswordException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
