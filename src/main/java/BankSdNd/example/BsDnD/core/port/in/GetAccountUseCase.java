package BankSdNd.example.BsDnD.core.port.in;

import BankSdNd.example.BsDnD.core.domain.model.Account;
import java.util.List;

/**
 * Input port for account retrieval use cases.
 */
public interface GetAccountUseCase {
    List<Account> searchClientAccount(String cpf);
    List<Account> findAllActive();
    List<Account> findAllByUserCpf(String cpf);
    boolean isAccountOwner(Long accountId, Long userId);
    boolean isAccountNumberOwner(String accountNumber, Long userId);
}
