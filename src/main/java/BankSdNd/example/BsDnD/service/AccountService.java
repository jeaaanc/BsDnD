package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.repository.AccountRepository;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BankUserRepository bankUserRepository;

    private static final BigDecimal FIXED_BONUS = new BigDecimal("300");
    private static final BigDecimal VARIABLE_BONUS = new BigDecimal("0.3");

    // TODO: Implementar validação de saldo mínimo para nova conta

    public Account accountCreate(String cpf, String accountNumber){
        BankUser user = bankUserRepository.findByCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        BigDecimal bonusvariable = user.getIncome().multiply(VARIABLE_BONUS);
        BigDecimal initialBalance = FIXED_BONUS.add(bonusvariable);

        Account account = new Account(accountNumber, user, initialBalance);

        return accountRepository.save(account);
    }

    public Account loginByAccountNumber(String accountNumber){
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("\nConta não encontrada"));
    }

    public List<Account> searchClientAccount(String cpf){
        List<Account> accounts = accountRepository.findAllByCpf(cpf);

        if (accounts.isEmpty()){
            throw new RuntimeException("Nenhuma conta encontrada neste CPF. ");
        }
        return accounts;
    }


    @Transactional
    public void transfer(String originNumberAccount, String destinationAccountNumber, BigDecimal value){
         Account origin = accountRepository.findByAccountNumber(originNumberAccount)
                 .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada."));

         Account destination = accountRepository.findByAccountNumber(destinationAccountNumber)
                 .orElseThrow(() -> new RuntimeException("Conta de destino não encontrada."));

         if (value.compareTo(BigDecimal.ZERO) <= 0){
             throw new RuntimeException("O valor da transferência deve ser maior que zero.");
         }

         if (origin.getBalance().compareTo(value) < 0) {
             throw new RuntimeException("Saldo insuficiente na conta de origem.");
         }


         origin.transferTo(destination, value);

        System.out.println("\nTransferencia feita com sucesso\n");

         accountRepository.save(origin);
         accountRepository.save(destination);

    }

    //Criar methodo para pedir a senha novamente
}
