package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.model.Account;
import BankSdNd.example.BsDnD.model.BankUser;
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

    // TODO: Implementar validação de saldo mínimo para nova conta

    public Account accountCreate(String cpf, String accountNumber, BigDecimal balance){
        BankUser user = bankUserRepository.findByCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        Account account = new Account(accountNumber, balance, user);
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
    //mudar o nome para ingles v
    public Account transferenciaEntreContas(){

        return null;
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

         origin.setBalance(origin.getBalance().subtract(value));
         destination.setBalance(destination.getBalance().add(value));

         accountRepository.save(origin);
         accountRepository.save(destination);

    }
}
