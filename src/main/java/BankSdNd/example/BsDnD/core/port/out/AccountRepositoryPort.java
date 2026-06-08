package BankSdNd.example.BsDnD.core.port.out;

import BankSdNd.example.BsDnD.core.domain.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepositoryPort {
    List<Account> findAllByCpf(String cpf);
    Optional<Account> findByAccountNumberAndActiveTrue(String accountNumber);
    Optional<Account> findById(Long id);
    Account save(Account account);
    List<Account> findAllByActiveTrue();
    boolean existsByAccountNumberAndActiveTrue(String accountNumber);
    void deleteAll();
}
