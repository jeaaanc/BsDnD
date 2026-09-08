package BankSdNd.example.BsDnD.core.port.in;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.dto.LoginCommand;
import java.util.UUID;

/**
 * Input port for user authentication use cases.
 */
public interface AuthenticateUserUseCase {
    BankUser login(LoginCommand command);
    void validatePassword(UUID userId, String rawPassword);
}
