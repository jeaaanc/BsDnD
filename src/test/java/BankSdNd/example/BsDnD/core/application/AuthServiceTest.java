package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.domain.exception.*;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.dto.LoginCommand;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private BankUserRepositoryPort userRepository;
    private PasswordEncoderPort passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = mock(BankUserRepositoryPort.class);
        passwordEncoder = mock(PasswordEncoderPort.class);
        authService = new AuthService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should return user when login credentials are valid")
    void login_ShouldReturnUserWhenValid() {
        LoginCommand command = new LoginCommand("12345678900", "password");
        BankUser user = BankUser.builder().cpf("12345678900").password("encoded").build();
        when(userRepository.findByCpf("12345678900")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);

        BankUser result = authService.login(command);

        assertEquals(user, result);
    }

    @Test
    @DisplayName("Should throw InvalidInputException when CPF is missing during login")
    void login_ShouldThrowInvalidInputExceptionWhenMissingCpf() {
        LoginCommand command = new LoginCommand("", "password");
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> authService.login(command));
        assertEquals("error.cpf_required", exception.getMessageKey());
    }

    @Test
    @DisplayName("Should throw InvalidPasswordException when password is wrong during login")
    void login_ShouldThrowInvalidPasswordExceptionWhenPasswordWrong() {
        LoginCommand command = new LoginCommand("12345678900", "wrong");
        BankUser user = BankUser.builder().cpf("12345678900").password("encoded").build();
        when(userRepository.findByCpf("12345678900")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> authService.login(command));
        assertEquals("error.password_incorrect", exception.getMessageKey());
    }

    @Test
    @DisplayName("Should update login password when valid")
    void updatePassword_ShouldUpdateWhenValid() {
        BankUser user = BankUser.builder().password("oldEncoded").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "oldEncoded")).thenReturn(true);
        when(passwordEncoder.encode("654321")).thenReturn("newEncoded");

        authService.updatePassword(1L, "123456", "654321");

        assertEquals("newEncoded", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw ValidationException when new password is not 6 digits")
    void updatePassword_ShouldThrowValidationExceptionWhenNot6Digits() {
        ValidationException exception = assertThrows(ValidationException.class, () -> authService.updatePassword(1L, "123456", "123"));
        assertEquals("error.password_length_login", exception.getMessageKey());
    }

    @Test
    @DisplayName("Should throw ValidationException when new password is same as old")
    void updatePassword_ShouldThrowValidationExceptionWhenSamePassword() {
        ValidationException exception = assertThrows(ValidationException.class, () -> authService.updatePassword(1L, "123456", "123456"));
        assertEquals("error.password_same", exception.getMessageKey());
    }

    @Test
    @DisplayName("Should throw ValidationException when new transaction password is not 4 digits")
    void updateTransactionPassword_ShouldThrowValidationExceptionWhenNot4Digits() {
        ValidationException exception = assertThrows(ValidationException.class, () -> authService.updateTransactionPassword(1L, "1234", "123"));
        assertEquals("error.password_length_transaction", exception.getMessageKey());
    }
}
