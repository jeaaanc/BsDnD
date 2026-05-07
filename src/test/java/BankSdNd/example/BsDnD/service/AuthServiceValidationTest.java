package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.repository.BankUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceValidationTest {

    private AuthService authService;
    private BankUserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(BankUserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
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
