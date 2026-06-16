package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.exception.*;
import BankSdNd.example.BsDnD.core.port.in.AuthenticateUserUseCase;
import BankSdNd.example.BsDnD.core.port.in.ManageCredentialsUseCase;
import BankSdNd.example.BsDnD.core.port.in.dto.LoginCommand;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;


/**
 * Service class responsible for user authentication and credential management.
 * <p>
 * This service handles the core security logic, including user login,
 * password validation for sensitive operations, and password changes.
 * It uses a {@link PasswordEncoderPort}
 * to securely compare and encode passwords.
 */
public class AuthService implements AuthenticateUserUseCase, ManageCredentialsUseCase {

    private final BankUserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public AuthService(BankUserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user based on the provided CPF and password.
     *
     * @param command the {@code LoginCommand} object containing the CPF and the raw password. Must not be null.
     * @return the complete {@code BankUser} object if authentication is successful.
     * @throws UserNotFoundException    if no user is found with the provided CPF.
     * @throws InvalidPasswordException if the provided password does not match the stored password.
     * @throws InvalidInputException if the CPF or password in the command are null or empty.
     */
    public BankUser login(LoginCommand command) {

        if (command == null || command.cpf() == null || command.cpf().isBlank()) {
            throw new InvalidInputException("error.cpf_required");
        }

        if (command.password() == null || command.password().isBlank()) {
            throw new InvalidInputException("error.password_required");
        }

        BankUser user = userRepository.findByCpf(command.cpf())
                .orElseThrow(() -> new UserNotFoundException("error.password_incorrect"));

        boolean matches = passwordEncoder.matches(command.password(), user.getPassword());

        if (!matches) {
            throw new InvalidPasswordException("error.password_incorrect");
        }

        return user;
    }

    /**
     * Validates if a given raw password matches the stored password for a specific user ID.
     * This method is typically used to re-authenticate a user before a sensitive operation.
     * The method completes successfully if the password is valid.
     *
     * @param userId      The ID of the user whose password is to be validated. Must not be null.
     * @param rawPassword The raw password to be checked. Must not be null or empty.
     * @throws UserNotFoundException    if no user is found for the given {@code userId}.
     * @throws InvalidPasswordException if the provided {@code rawPassword} is empty or does not match the user's stored password.
     */
    public void validatePassword(Long userId, String rawPassword) {

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new InvalidPasswordException("error.password_required");
        }

        BankUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("error.user_not_found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidPasswordException("error.password_incorrect");
        }
    }

    /**
     * Changes a user's password after validating their old password.
     * The operation is transactional, ensuring that the change only completes if all steps are successful.
     *
     * @param userId      The ID of the user whose password is to be changed.
     * @param oldPassword The user's current (old) password.
     * @param newPassword The user's new password.
     * @throws UserNotFoundException    if no user is found for the given {@code userId}.
     * @throws InvalidPasswordException if the provided {@code oldPassword} does not match the stored password.
     * @throws InvalidInputException if any of the passwords are null/empty, or if the new password is the same as the old one.
     */
    public void updatePassword(Long userId, String oldPassword, String newPassword) {

        if (oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            throw new InvalidInputException("error.password_required");
        }

        if (!newPassword.matches("^\\d{6}$")) {
            throw new ValidationException("error.password_length_login");
        }

        if (oldPassword.equals(newPassword)) {
            throw new ValidationException("error.password_same");
        }

        BankUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("error.user_not_found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("error.password_incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    /**
     * Changes a user's transaction password after validating their old transaction password.
     *
     * @param userId                 The ID of the user.
     * @param oldTransactionPassword The current transaction password.
     * @param newTransactionPassword The new transaction password.
     * @throws UserNotFoundException    if user is not found.
     * @throws InvalidPasswordException if old transaction password is incorrect.
     */
    public void updateTransactionPassword(Long userId, String oldTransactionPassword, String newTransactionPassword) {
        validatePasswordInputs(oldTransactionPassword, newTransactionPassword);

        if (!newTransactionPassword.matches("^\\d{4}$")) {
            throw new ValidationException("error.password_length_transaction");
        }

        BankUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("error.user_not_found"));

        if (user.getTransactionPassword() != null && !passwordEncoder.matches(oldTransactionPassword, user.getTransactionPassword())) {
            throw new InvalidPasswordException("error.password_incorrect");
        }

        String encodedNewTransactionPassword = passwordEncoder.encode(newTransactionPassword);
        user.setTransactionPassword(encodedNewTransactionPassword);
        userRepository.save(user);
    }

    private void validatePasswordInputs(String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.isBlank()
                || newPassword == null || newPassword.isBlank()) {
            throw new InvalidInputException("error.password_required");
        }
        if (oldPassword.equals(newPassword)){
            throw new ValidationException("error.password_same");
        }
    }
}
