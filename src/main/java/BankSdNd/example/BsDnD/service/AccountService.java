package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.exception.business.*;
import BankSdNd.example.BsDnD.repository.AccountRepository;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import BankSdNd.example.BsDnD.util.validation.AccountNumberGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import org.springframework.security.access.AccessDeniedException;
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
    private PasswordEncoder passwordEncoder;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountService(AccountRepository accountRepository,
                          BankUserRepository bankUserRepository,
                          AccountNumberGenerator accountNumberGenerator,
                          PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.bankUserRepository = bankUserRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new bank account for an existing user, identified by their CPF.
     * The initial balance is zero.
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

        String accountNumber = accountNumberGenerator.generateUniqueAccountNumber();
        Account account = new Account(accountNumber, user);

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
     * @param transactionPassword      The 4-digit transactional password of the account holder.
     * @throws AccountNotFoundException     if either the origin or destination account is not found.
     * @throws InsufficientBalanceException if the origin account does not have enough balance (thrown from the Account entity).
     * @throws AccessDeniedException        if the logged user is not the owner of the origin account.
     * @throws BusinessException            if the tra transaction password is invalid.
     */
    @Transactional
    public void transfer(String originAccountNumber, String destinationAccountNumber, BigDecimal value, String transactionPassword) {

        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientBalanceException("The tranfer amount must be greater than 0.");
        }

        BankUser loggedUser = (BankUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account origin = accountRepository.findByAccountNumberAndActiveTrue(originAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account: " + originAccountNumber + " not found."));
        
        if (!origin.getHolder().getId().equals(loggedUser.getId())) {
            throw new AccessDeniedException("Você não tem permisão para usar esta conta.");
        }

        if (!passwordEncoder.matches(transactionPassword, origin.getHolder().getTransactionPassword())) {
            throw new BusinessException("Invalid transaction password. Transfer denied. ");
        }

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

        if (!account.getHolder().getId().equals(userId)) {
            throw new UnauthorizedOperationException("Origin account does not belong to the user.");
        }
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

    public List<Account> findAllActive() {
        return accountRepository.findAllByActiveTrue();
    }

    public List<Account> findAllByUserCpf(String cpf) {
        return accountRepository.findAllByCpf(cpf);
    }
}
