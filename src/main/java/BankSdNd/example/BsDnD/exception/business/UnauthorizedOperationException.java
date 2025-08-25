package BankSdNd.example.BsDnD.exception.business;

public class UnauthorizedOperationException extends BusinessException{
    public UnauthorizedOperationException(String message) {
        super(message);
    }

    public UnauthorizedOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
