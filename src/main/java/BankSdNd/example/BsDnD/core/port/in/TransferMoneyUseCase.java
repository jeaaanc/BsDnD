package BankSdNd.example.BsDnD.core.port.in;

import java.math.BigDecimal;

/**
 * Input port for money transfer use cases.
 */
public interface TransferMoneyUseCase {
    void transfer(String originAccountNumber, String destinationAccountNumber, BigDecimal value, String transactionPassword);
    void transfer(String originAccountNumber, String destinationAccountNumber, BigDecimal value, char[] transactionPassword);
}
