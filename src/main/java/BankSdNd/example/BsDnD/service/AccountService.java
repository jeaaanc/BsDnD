package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.exception.business.*;
import BankSdNd.example.BsDnD.repository.AccountRepository;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import BankSdNd.example.BsDnD.util.validation.AccountNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


/**
 * Service class for managing bank account operations.
 * <p>
 * This service handles the business logic for creating new accounts,
 * retrieving account information, performing transfers, and validating account ownership.
 * All state-changing methods are transactional.
 */
@Service
public class AccountService {

    private AccountRepository accountRepository;
    private BankUserRepository bankUserRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    private static final BigDecimal FIXED_BONUS = new BigDecimal("300");
    private static final BigDecimal VARIABLE_BONUS = new BigDecimal("0.3");
    // TODO: Implementar validação de saldo mínimo para nova conta

    public AccountService(AccountRepository accountRepository,
                          BankUserRepository bankUserRepository,
                          AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.bankUserRepository = bankUserRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    /**
     * Creates a new bank account for an existing user, identified by their CPF.
     * The initial balance is calculated based on a fixed bonus and a percentage of the user's income.
     * This operation is transactional.
     *
     * @param cpf The CPF of the BankUser who will own the new account. Must not be null.
     * @return The newly created and persisted Account object.
     * @throws UserNotFoundException if no user is found with the given CPF.
     */
    @Transactional
    public Account accountCreate(String cpf) {
        BankUser user = bankUserRepository.findByCpf(cpf)
                .orElseThrow(() -> new UserNotFoundException("User with CPF " + cpf + " not found"));

        BigDecimal bonusvariable = user.getIncome().multiply(VARIABLE_BONUS);
        BigDecimal initialBalance = FIXED_BONUS.add(bonusvariable);

        String accountNumber = accountNumberGenerator.generateUniqueAccountNumber();
        Account account = new Account(accountNumber, user, initialBalance);

        return accountRepository.save(account);
    }

    /**
     * Finds all bank accounts associated with a user's CPF.
     *
     * @param cpf The CPF of the user whose accounts are to be retrieved.
     * @return A {@code List<Account>} containing all found accounts for the user.
     * @throws UserNotFoundException if no accounts are found for the given CPF.
     */
    public List<Account> searchClientAccount(String cpf) {
        List<Account> accounts = accountRepository.findAllByCpf(cpf);

        if (accounts.isEmpty()) {
            throw new UserNotFoundException("No account found for the given CPF: " + cpf);
        }
        return accounts;
    }

    /**
     * Performs a monetary transfer between two accounts. This operation is transactional.
     * If any validation fails (e.g., insufficient balance), the entire operation is rolled back.
     *
     * @param originAccountNumber      The account number of the source account.
     * @param destinationAccountNumber The account number of the destination account.
     * @param value                    The amount to be transferred. Must be positive.
     * @throws AccountNotFoundException     if either the origin or destination account is not found.
     * @throws InsufficientBalanceException if the origin account does not have enough balance (thrown from the Account entity).
     * @throws IllegalArgumentException     if the transfer value is not positive or another argument is invalid.
     */
    @Transactional
    public void transfer(String originAccountNumber, String destinationAccountNumber, BigDecimal value) {

        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientBalanceException("The tranfer amount must be greater than 0.");
        }

        Account origin = accountRepository.findByAccountNumberAndActiveTrue(originAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account: " + originAccountNumber + " not found."));

        Account destination = accountRepository.findByAccountNumberAndActiveTrue(destinationAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Destination " + destinationAccountNumber + " not found."));

        origin.transferTo(destination, value);

    }

    /**
     * Verifies if a given account number belongs to a specific user.
     * This is a security method to ensure a user can only operate on their own accounts.
     * The method completes successfully if ownership is valid.
     *
     * @param userId        The ID of the user claiming ownership.
     * @param accountNumber The account number to be checked.
     * @throws AccountNotFoundException       if the account number does not exist.
     * @throws UnauthorizedOperationException if the account exists but does not belong to the specified user.
     */
    public void validateAccountOwnership(Long userId, String accountNumber) {
        Account account = accountRepository.findByAccountNumberAndActiveTrue(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found."));

        if (!account.getTitular().getId().equals(userId)) {
            throw new UnauthorizedOperationException("Origin account does not belong to the user.");
        }
    }

    // !! fazer comentarios !!!!!!!

    @Transactional
    public void softDeleteAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {

            throw new BusinessException("Cannot delete account with a remaining balance." +
                    "Please withdraw the funds first.");
        }

        account.setActive(false);
        accountRepository.save(account);
    }
}
