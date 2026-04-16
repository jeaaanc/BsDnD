package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import java.math.BigDecimal;
import java.util.List;

/**
 * Interface to centralize financial calculations within the bank.
 * <p>
 * Defines the necessary methods to calculate loan limits, credit limits,
 * and other financial operations based on user data and their accounts.
 */
public interface FinancialCalculator {

    /**
     * Calculates the available loan limit for a user.
     *
     * @param user The user for whom the limit will be calculated.
     * @param accounts The list of the user's active accounts.
     * @return The loan limit value.
     */
    BigDecimal calculateLoanLimit(BankUser user, List<Account> accounts);

    /**
     * Calculates the VIP loan limit for eligible users.
     *
     * @param user The user for whom the limit will be calculated.
     * @param accounts The list of the user's active accounts.
     * @return The VIP loan limit value.
     */
    BigDecimal calculateVipLoanLimit(BankUser user, List<Account> accounts);

    /**
     * Calculates the available credit limit for the user.
     *
     * @param user The user for whom the limit will be calculated.
     * @param accounts The list of the user's active accounts.
     * @return The credit limit value.
     */
    BigDecimal calculateCreditLimit(BankUser user, List<Account> accounts);
}
