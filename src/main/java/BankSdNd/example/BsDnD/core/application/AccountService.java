package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.exception.*;
import BankSdNd.example.BsDnD.core.port.in.CreateAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.GetAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManageAccountUseCase;
import BankSdNd.example.BsDnD.core.port.in.TransferMoneyUseCase;
import BankSdNd.example.BsDnD.core.port.out.AccountRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import BankSdNd.example.BsDnD.core.domain.service.AccountNumberGenerator;

import java.math.BigDecimal;
import java.util.List;


/**
 * Service class for managing bank account operations.
 * <p>
 * This service handles the business logic for creating new accounts,
 * retrieving account information, performing transfers, and validating account ownership.
 * All state-changing methods are transactional.
 */
public class AccountService implements CreateAccountUseCase, GetAccountUseCase, ManageAccountUseCase, TransferMoneyUseCase {

    private AccountRepositoryPort accountRepository;
    private BankUserRepositoryPort bankUserRepository;
    private PasswordEncoderPort passwordEncoder;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountService(AccountRepositoryPort accountRepository,
                          BankUserRepositoryPort bankUserRepository,
                          AccountNumberGenerator accountNumberGenerator,
                          PasswordEncoderPort passwordEncoder) {
        this.accountRepository = accountRepository;
        this.bankUserRepository = bankUserRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new bank account for an existing user.
     * Validates the user's transaction password and ensures they don't already have an active account.
     *
     * @param cpf                 The CPF of the user.
     * @param transactionPassword The user's 4-digit transaction password as a String.
     * @return The newly created Account.
     * @throws UserNotFoundException    if user not found.
     * @throws BusinessException         if password is wrong or account already exists.
     */
    public Account createAccount(String cpf, String transactionPassword) {
        BankUser user = bankUserRepository.findByCpf(cpf)
                .orElseThrow(() -> new UserNotFoundException("error.user_not_found"));

        if (!passwordEncoder.matches(transactionPassword, user.getTransactionPassword())) {
            throw new InvalidTrasctionPasswordException("error.password_incorrect");
        }


        List<Account> existingAccounts = accountRepository.findAllByCpf(cpf);
        if (existingAccounts.size() >= 3) {
            throw new BusinessException("error.account_limit");
        }

        String accountNumber = accountNumberGenerator.generateUniqueAccountNumber();
        Account account = new Account(accountNumber, user);

        return accountRepository.save(account);
    }

    /**
     * Overloaded method for creating an account using a char array for the password.
     * Securely converts the char array to a String, performs the operation, and clears the array.
     */
    public Account createAccount(String cpf, char[] transactionPassword) {
        try {
            return createAccount(cpf, new String(transactionPassword));
        } finally {
            if (transactionPassword != null) {
                java.util.Arrays.fill(transactionPassword, '\0');
            }
        }
    }

    /**
     * Finds all bank accounts associated with a user's CPF.
     *
     * @param cpf The CPF of the user whose accounts are to be retrieved.
     * @return A {@code List<Account>} containing all found accounts for the user.
     */
    public List<Account> searchClientAccount(String cpf) {
        return accountRepository.findAllByCpf(cpf);
    }

    /**
     * Performs a monetary transfer between two accounts. This operation is transactional.
     * If any validation fails (e.g., insufficient balance), the entire operation is rolled back.
     *
     * @param originAccountNumber      The account number of the source account.
     * @param destinationAccountNumber The account number of the destination account.
     * @param value                    The amount to be transferred. Must be positive.
     * @param transactionPassword      The 4-digit transactional password of the account holder as a String.
     * @throws AccountNotFoundException     if either the origin or destination account is not found.
     * @throws InsufficientBalanceException if the origin account does not have enough balance (thrown from the Account entity).
     * @throws BusinessException            if the tra transaction password is invalid.
     */
    public void transfer(String originAccountNumber, String destinationAccountNumber, BigDecimal value, String transactionPassword) {

        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("error.amount_positive");
        }

        Account origin = accountRepository.findByAccountNumberAndActiveTrue(originAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("error.account_not_found"));

        if (!passwordEncoder.matches(transactionPassword, origin.getHolder().getTransactionPassword())) {
            throw new BusinessException("error.password_incorrect");
        }

        Account destination = accountRepository.findByAccountNumberAndActiveTrue(destinationAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("error.account_not_found"));


        origin.transferTo(destination, value);

        accountRepository.save(origin);
        accountRepository.save(destination);
    }

    /**
     * Overloaded method for performing a transfer using a char array for the password.
     * Securely converts the char array to a String, performs the operation, and clears the array.
     */
    public void transfer(String originAccountNumber, String destinationAccountNumber, BigDecimal value, char[] transactionPassword) {
        try {
            transfer(originAccountNumber, destinationAccountNumber, value, new String(transactionPassword));
        } finally {
            if (transactionPassword != null) {
                java.util.Arrays.fill(transactionPassword, '\0');
            }
        }
    }

    public boolean isAccountOwner(Long accountId, Long userId) {
        return accountRepository.findById(accountId)
                .map(account -> account.isOwnedBy(userId))
                .orElse(false);
    }

    public boolean isAccountNumberOwner(String accountNumber, Long userId) {
        return accountRepository.findByAccountNumberAndActiveTrue(accountNumber)
                .map(account -> account.isOwnedBy(userId))
                .orElse(false);
    }

    /**
     * Executes a logical deletion (Soft Delete) of a bank account by setting its status to inactive.
     * Includes a business rule validation to prevent closing accounts with a remaining balance.
     * The operation is transactional to ensure database consistency.
     *
     * @param accountId The unique identifier of the account to be deactivated.
     * @throws AccountNotFoundException if the account ID is not found in the database.
     * @throws BusinessException        if the account balance is greater than zero.
     */
    public void softDeleteAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("error.account_not_found"));

        if (!account.isActive()) {
            throw new BusinessException("error.account_closed");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {

            throw new BusinessException("error.account_balance_remaining");
        }

        account.setActive(false);
        accountRepository.save(account);
    }

    public List<Account> findAllActive() {
        return accountRepository.findAllByActiveTrue();
    }

    public List<Account> findAllByUserCpf(String cpf) {
        return accountRepository.findAllByCpf(cpf);
    }
}
