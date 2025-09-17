package BankSdNd.example.BsDnD.exception.business;

public class LoanLimitExceededException  extends  BusinessException{
    public LoanLimitExceededException(String message) {
        super(message);
    }
}
