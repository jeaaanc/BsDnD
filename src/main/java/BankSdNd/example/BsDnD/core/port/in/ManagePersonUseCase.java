package BankSdNd.example.BsDnD.core.port.in;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;

import java.util.UUID;

/**
 * Input port for person management use cases.
 */
public interface ManagePersonUseCase {
    BankUser updatePhoneNumber(UUID userId, String newPhoneNumber);
    BankUser updateName(UUID userId, String newFirstName, String newLastName);
}
