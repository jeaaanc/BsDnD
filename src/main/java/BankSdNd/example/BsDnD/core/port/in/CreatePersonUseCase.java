package BankSdNd.example.BsDnD.core.port.in;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.dto.CreatePersonCommand;

/**
 * Input port for person creation use cases.
 */
public interface CreatePersonUseCase {
    BankUser savePerson(CreatePersonCommand command);
}
