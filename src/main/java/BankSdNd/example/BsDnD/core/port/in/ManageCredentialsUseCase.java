package BankSdNd.example.BsDnD.core.port.in;

import java.util.UUID;

/**
 * Input port for credential management use cases.
 */
public interface ManageCredentialsUseCase {
    void updatePassword(UUID userId, String oldPassword, String newPassword);
    void updateTransactionPassword(UUID userId, String oldTransactionPassword, String newTransactionPassword);
}
