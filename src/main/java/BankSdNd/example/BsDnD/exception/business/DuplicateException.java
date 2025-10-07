package BankSdNd.example.BsDnD.exception.business;

public class DuplicateException extends RuntimeException{
    public DuplicateException(String field, String value){
        super(field + " already registered: " + value);
    }
}
