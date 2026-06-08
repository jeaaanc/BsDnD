package BankSdNd.example.BsDnD.core.port.in;

/**
 * Input port for account management (update/delete) use cases.
 */
public interface ManageAccountUseCase {
    void softDeleteAccount(Long accountId);
}
