package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.exception.business.LoanLimitExceededException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.CharBuffer;
import java.util.List;



/**
 * Service class for handling business logic related to customer loans.
 *
 * This service is responsible for calculating available loan limits based on
 * customer data and for granting new loans in a transactional manner.
 */
@Service
public class LoanService {

    private final AccountService accountService;
    private final FinancialCalculator financialCalculator;
    private final PasswordEncoder passwordEncoder;

    public LoanService(AccountService accountService, FinancialCalculator financialCalculator, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.financialCalculator = financialCalculator;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Calculates the available loan limit for a customer based on their total balance across all accounts.
     * This is a pure business logic method with no user interaction.
     * The rule is defined by the FinancialCalculator implementation.
     *
     * @param user The customer for whom the limit will be calculated. Must not be null.
     * @return The loan limit value as a {@code BigDecimal}.
     */
    @PreAuthorize("hasRole('USER') and principal.username == #user.cpf")
    public BigDecimal calculateLoanLimit(BankUser user) {

        List<Account> accounts = accountService.searchClientAccount(user.getCpf());
        return financialCalculator.calculateLoanLimit(user, accounts);
    }

    /**
     * Grants a loan to a customer by depositing the requested amount into their primary account.
     * This operation is transactional. It first validates the requested amount against the calculated limit.
     *
     * @param user The customer who will receive the loan. Must not be null.
     * @param requestedAmount The amount requested for the loan.
     * @param transactionPassword The 4-digit transactional password of the account holder.
     * @return The {@code Account} that received the loan deposit, with its updated balance.
     * @throws LoanLimitExceededException if the requested amount is greater than the user's available limit.
     * @throws IllegalArgumentException if the requested amount is not positive, or if the user has no accounts to receive the deposit.
     */
    @Transactional
    @PreAuthorize("hasRole('USER') and principal.username == #user.cpf")
    public Account grantLoan(BankUser user, BigDecimal requestedAmount, char[] transactionPassword) {

        if (!passwordEncoder.matches(CharBuffer.wrap(transactionPassword), user.getTransactionPassword())) {
            throw new IllegalArgumentException("Invalid transaction password. Loan denied.");
        }

        BigDecimal limit = calculateLoanLimit(user);
        if (requestedAmount.compareTo(limit) > 0) {
            throw new LoanLimitExceededException("The request amount exceeds your loan limit of R$ " + limit);
        }
        if (requestedAmount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("The loan amount must be positive.");
        }

        Account primaryAccount = accountService.searchClientAccount(user.getCpf()).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The customer does not have an account to receive the loan."));

        primaryAccount.deposit(requestedAmount);
        return primaryAccount;
    }
}
