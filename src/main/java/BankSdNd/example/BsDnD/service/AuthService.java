package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.dto.LoginDto;
import BankSdNd.example.BsDnD.exception.business.InvalidPasswordException;
import BankSdNd.example.BsDnD.exception.business.UserNotFoundException;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service class responsible for user authentication and credential management.
 *
 * This service handles the core security logic, including user login,
 * password validation for sensitive operations, and password changes.
 * It uses a {@link org.springframework.security.crypto.password.PasswordEncoder}
 * to securely compare and encode passwords.
 */
@Service
public class AuthService {

    private final BankUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(BankUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user based on the provided CPF and password.
     *
     * @param dto the {@code LoginDto} object containing the CPF and the raw password. Must not be null.
     * @return the complete {@code BankUser} object if authentication is successful.
     * @throws UserNotFoundException    if no user is found with the provided CPF.
     * @throws InvalidPasswordException if the provided password does not match the stored password.
     * @throws IllegalArgumentException if the CPF or password in the DTO are null or empty.
     */
    public BankUser login(LoginDto dto) {

            if (dto == null || dto.cpf() == null || dto.cpf().isBlank()) {
                throw new IllegalArgumentException("CPF is required.");// excptio!!!
            }

            if (dto.password() == null || dto.password().isBlank()) {
                throw new IllegalArgumentException("Password is required.");//Exception
            }

            BankUser user = userRepository.findByCpf(dto.cpf())
                    .orElseThrow(() -> new UserNotFoundException("Incorrect user or password."));

            boolean matches = passwordEncoder.matches(dto.password(), user.getPassword());

            if (!matches) {
                throw new InvalidPasswordException("Incorrect user or password.");
            }

            return user;
    }

    /**
     * Validates if a given raw password matches the stored password for a specific user ID.
     * This method is typically used to re-authenticate a user before a sensitive operation.
     * The method completes successfully if the password is valid.
     *
     * @param userId The ID of the user whose password is to be validated. Must not be null.
     * @param rawPassword The raw password to be checked. Must not be null or empty.
     * @throws UserNotFoundException if no user is found for the given {@code userId}.
     * @throws InvalidPasswordException if the provided {@code rawPassword} is empty or does not match the user's stored password.
     */
    public void validatePassword(Long userId, String rawPassword) {

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new InvalidPasswordException("Confirmation password cannot be empty.");
        }

        BankUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for verification."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidPasswordException("Incorrect password! Operation canceled.");
        }
    }

    /**
     * Changes a user's password after validating their old password.
     * The operation is transactional, ensuring that the change only completes if all steps are successful.
     *
     * @param userId The ID of the user whose password is to be changed.
     * @param oldPassword The user's current (old) password.
     * @param newPassword The user's new password.
     * @throws UserNotFoundException if no user is found for the given {@code userId}.
     * @throws InvalidPasswordException if the provided {@code oldPassword} does not match the stored password.
     * @throws IllegalArgumentException if any of the passwords are null/empty, or if the new password is the same as the old one.
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {

        if (oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Passwords cannot be empty.");
        }
        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("The new password cannot be the same as the old one.");
        }

        BankUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect.");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

}
