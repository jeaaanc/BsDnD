package BankSdNd.example.BsDnD.core.port.in;

/**
 * Input port for credential management use cases.
 */
public interface ManageCredentialsUseCase {
    void updatePassword(Long userId, String oldPassword, String newPassword);
    void updateTransactionPassword(Long userId, String oldTransactionPassword, String newTransactionPassword);
}
