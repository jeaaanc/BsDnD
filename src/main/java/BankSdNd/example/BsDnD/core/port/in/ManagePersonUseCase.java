package BankSdNd.example.BsDnD.core.port.in;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;

/**
 * Input port for person management use cases.
 */
public interface ManagePersonUseCase {
    BankUser updatePhoneNumber(Long userId, String newPhoneNumber);
    BankUser updateName(Long userId, String newFirstName, String newLastName);
}
