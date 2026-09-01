package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.domain.exception.*;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.port.in.dto.LoginCommand;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private BankUserRepositoryPort userRepository;
    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private final String DEFAULT_CPF = "12345678900";
    private final Long DEFAULT_USER_ID = 1L;

    private final String VALID_PASSWORD = "password";
    private final String ENCODED_PASSWORD = "encoded";
    private final String WRONG_PASSWORD = "wrong";
    private final String OLD_RAW_PASSWORD = "123456";
    private final String OLD_ENCODED_PASSWORD = "oldEncoded";
    private final String NEW_RAW_PASSWORD = "654321";
    private final String NEW_ENCODED_PASSWORD = "newEncoded";

    private final String INVALID_SHORT_PASSWORD = "123";
    private final String VALID_TX_PASSWORD = "1234";


    @BeforeEach
    void setUp() {
        userRepository = mock(BankUserRepositoryPort.class);
        passwordEncoder = mock(PasswordEncoderPort.class);
        authService = new AuthService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should return user when login credentials are valid")
    void login_ShouldReturnUserWhenValid() {

        LoginCommand command = new LoginCommand(DEFAULT_CPF, VALID_PASSWORD);
        BankUser user = BankUser.builder().cpf(DEFAULT_CPF).password(ENCODED_PASSWORD).build();

        when(userRepository.findByCpf(DEFAULT_CPF)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(VALID_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        BankUser result = authService.login(command);

        assertEquals(user, result);
    }

    @Test
    @DisplayName("Should throw InvalidInputException when CPF is missing during login")
    void login_ShouldThrowInvalidInputExceptionWhenMissingCpf() {

        LoginCommand command = new LoginCommand("", VALID_PASSWORD);

        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> authService.login(command)
        );

        assertEquals("error.cpf_required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidPasswordException when password is wrong during login")
    void login_ShouldThrowInvalidPasswordExceptionWhenPasswordWrong() {

        LoginCommand command = new LoginCommand(DEFAULT_CPF, WRONG_PASSWORD);
        BankUser user = BankUser.builder().cpf(DEFAULT_CPF).password(ENCODED_PASSWORD).build();

        when(userRepository.findByCpf(DEFAULT_CPF)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(WRONG_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class,
                () -> authService.login(command)
        );

        assertEquals("error.password_incorrect", exception.getMessage());
    }

    @Test
    @DisplayName("Should update login password when valid")
    void updatePassword_ShouldUpdateWhenValid() {

        BankUser user = BankUser.builder().password(OLD_ENCODED_PASSWORD).build();

        when(userRepository.findById(DEFAULT_USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(OLD_RAW_PASSWORD, OLD_ENCODED_PASSWORD)).thenReturn(true);
        when(passwordEncoder.encode(NEW_RAW_PASSWORD)).thenReturn(NEW_ENCODED_PASSWORD);

        authService.updatePassword(DEFAULT_USER_ID, OLD_RAW_PASSWORD, NEW_RAW_PASSWORD);

        assertEquals(NEW_ENCODED_PASSWORD, user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw ValidationException when new password is not 6 digits")
    void updatePassword_ShouldThrowValidationExceptionWhenNot6Digits() {

        ValidationException exception = assertThrows(ValidationException.class, () ->
                authService.updatePassword(DEFAULT_USER_ID, OLD_RAW_PASSWORD, INVALID_SHORT_PASSWORD)
        );

        assertEquals("error.password_length_login", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ValidationException when new password is same as old")
    void updatePassword_ShouldThrowValidationExceptionWhenSamePassword() {

        ValidationException exception = assertThrows(ValidationException.class, () ->
                authService.updatePassword(DEFAULT_USER_ID, OLD_RAW_PASSWORD, OLD_RAW_PASSWORD)
        );

        assertEquals("error.password_same", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ValidationException when new transaction password is not 4 digits")
    void updateTransactionPassword_ShouldThrowValidationExceptionWhenNot4Digits() {

        ValidationException exception = assertThrows(ValidationException.class, () ->
                authService.updateTransactionPassword(DEFAULT_USER_ID, VALID_TX_PASSWORD, INVALID_SHORT_PASSWORD)
        );

        assertEquals("error.password_length_transaction", exception.getMessage());
    }
}
