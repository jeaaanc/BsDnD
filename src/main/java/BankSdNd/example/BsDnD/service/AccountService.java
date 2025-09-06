package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.exception.business.AccountNotFoundException;
import BankSdNd.example.BsDnD.exception.business.InsufficientBalanceException;
import BankSdNd.example.BsDnD.exception.business.UnauthorizedOperationException;
import BankSdNd.example.BsDnD.exception.business.UserNotFoundException;
import BankSdNd.example.BsDnD.repository.AccountRepository;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import BankSdNd.example.BsDnD.util.validation.AccountNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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

    @Transactional
    public Account accountCreate(String cpf){
        BankUser user = bankUserRepository.findByCpf(cpf)
                .orElseThrow(() -> new UserNotFoundException("Usuário com CPF " + cpf + " não encontrado"));

        BigDecimal bonusvariable = user.getIncome().multiply(VARIABLE_BONUS);
        BigDecimal initialBalance = FIXED_BONUS.add(bonusvariable);

        String  accountNumber = accountNumberGenerator.generateUniqueAccountNumber();
        Account account = new Account(accountNumber, user, initialBalance);

        return accountRepository.save(account);
    }

    //!!!!!olhar
    public Account loginByAccountNumber(String accountNumber){
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException("\nConta: " + accountNumber + " não encontrada"));
    }

    public List<Account> searchClientAccount(String cpf){
        List<Account> accounts = accountRepository.findAllByCpf(cpf);

        if (accounts.isEmpty()){
            throw new UserNotFoundException("Nenhuma conta encontrada no CPF: " + cpf);
        }
        return accounts;
    }

// arrumar as exception!!!!!!!!!!!
    @Transactional
    public void transfer(String originNumberAccount, String destinationAccountNumber, BigDecimal value){

        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0){
            throw new InsufficientBalanceException("O valor da transferência deve ser maior que 0.");
        }

         Account origin = accountRepository.findByAccountNumber(originNumberAccount)
                 .orElseThrow(() -> new AccountNotFoundException("Conta: " + originNumberAccount +". Não encontrada!!!"));

         Account destination = accountRepository.findByAccountNumber(destinationAccountNumber)
                 .orElseThrow(() -> new AccountNotFoundException("Conta de destino: " + originNumberAccount + ". Não encontrada"));

         origin.transferTo(destination, value);

//         accountRepository.save(origin);
//         accountRepository.save(destination);
    }

    public void validateAccountOwnership(Long userId, String accountNumber){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada!"));

        if (!account.getTitular().getId().equals(userId)){
            throw new UnauthorizedOperationException("Conta de origem não pertence ao usuário!");
        }
    }

}
