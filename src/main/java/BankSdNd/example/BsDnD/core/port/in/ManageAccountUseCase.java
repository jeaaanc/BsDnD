package BankSdNd.example.BsDnD.core.port.in;

import java.util.UUID;

/**
 * Input port for account management (update/delete) use cases.
 */
public interface ManageAccountUseCase {
    void softDeleteAccount(UUID accountId);
}
