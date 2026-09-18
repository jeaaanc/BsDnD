package BankSdNd.example.BsDnD.core.port.in;

import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import java.math.BigDecimal;

/**
 * Input port for loan related use cases.
 */
public interface RequestLoanUseCase {
    BigDecimal calculateLoanLimit(BankUser user);
    Account grantLoan(BankUser user, BigDecimal requestedAmount, char[] transactionPassword);
}
