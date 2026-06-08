package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceValidationTest {

    private AuthService authService;
    private BankUserRepositoryPort userRepository;
    private PasswordEncoderPort passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(BankUserRepositoryPort.class);
        passwordEncoder = Mockito.mock(PasswordEncoderPort.class);
        authService = new AuthService(userRepository, passwordEncoder);
    }

    @Test
    void shouldThrowExceptionWhenNewPasswordDoesNotHave6Digits() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.updatePassword(1L, "123456", "12345");
        });
        assertEquals("A senha de login deve ter exatamente 6 números.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.updatePassword(1L, "123456", "1234567");
        });
        assertEquals("A senha de login deve ter exatamente 6 números.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.updatePassword(1L, "123456", "abcdef");
        });
        assertEquals("A senha de login deve ter exatamente 6 números.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNewTransactionPasswordDoesNotHave4Digits() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.updateTransactionPassword(1L, "1234", "123");
        });
        assertEquals("A senha de transação deve ter exatamente 4 números.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.updateTransactionPassword(1L, "1234", "12345");
        });
        assertEquals("A senha de transação deve ter exatamente 4 números.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.updateTransactionPassword(1L, "1234", "abcd");
        });
        assertEquals("A senha de transação deve ter exatamente 4 números.", exception.getMessage());
    }
}
