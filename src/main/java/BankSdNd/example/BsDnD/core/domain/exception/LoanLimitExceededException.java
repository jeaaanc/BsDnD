package BankSdNd.example.BsDnD.core.domain.exception;

public class LoanLimitExceededException extends BusinessException {
    public LoanLimitExceededException(String messageKey) {
        super(messageKey);
    }

    public LoanLimitExceededException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
