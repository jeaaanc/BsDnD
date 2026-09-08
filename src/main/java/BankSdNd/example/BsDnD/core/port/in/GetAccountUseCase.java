package BankSdNd.example.BsDnD.core.port.in;

import BankSdNd.example.BsDnD.core.domain.model.Account;
import java.util.List;
import java.util.UUID;

/**
 * Input port for account retrieval use cases.
 */
public interface GetAccountUseCase {
    List<Account> searchClientAccount(String cpf);
    List<Account> findAllActive();
    List<Account> findAllByUserCpf(String cpf);
    boolean isAccountOwner(UUID accountId, UUID userId);
    boolean isAccountNumberOwner(String accountNumber, UUID userId);
}
