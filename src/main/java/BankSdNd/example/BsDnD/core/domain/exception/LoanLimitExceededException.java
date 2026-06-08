package BankSdNd.example.BsDnD.core.domain.exception;

public class LoanLimitExceededException extends BusinessException {
    public LoanLimitExceededException(String message) {
        super(message);
    }
}
