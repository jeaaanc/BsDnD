package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.domain.exception.*;
import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.service.AccountNumberGenerator;
import BankSdNd.example.BsDnD.core.port.out.AccountRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountService accountService;
    private AccountRepositoryPort accountRepository;
    private BankUserRepositoryPort bankUserRepository;
    private AccountNumberGenerator accountNumberGenerator;
    private PasswordEncoderPort passwordEncoder;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepositoryPort.class);
        bankUserRepository = mock(BankUserRepositoryPort.class);
        accountNumberGenerator = mock(AccountNumberGenerator.class);
        passwordEncoder = mock(PasswordEncoderPort.class);
        accountService = new AccountService(accountRepository, bankUserRepository, accountNumberGenerator, passwordEncoder);
    }

    @Test
    @DisplayName("Should create an account when user and password are valid")
    void createAccount_ShouldCreateWhenValid() {
        BankUser user = BankUser.builder().transactionPassword("encoded").build();
        when(bankUserRepository.findByCpf("12345678900")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq("encoded"))).thenReturn(true);
        when(accountRepository.findAllByCpf("12345678900")).thenReturn(Collections.emptyList());
        when(accountNumberGenerator.generateUniqueAccountNumber()).thenReturn("12345-6");
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Account account = accountService.createAccount("12345678900", "1234");

        assertNotNull(account);
        assertEquals("12345-6", account.getAccountNumber());
        verify(accountRepository).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when transaction password is wrong during creation")
    void createAccount_ShouldThrowBusinessExceptionWhenPasswordWrong() {
        BankUser user = BankUser.builder().transactionPassword("encoded").build();
        when(bankUserRepository.findByCpf("12345678900")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq("encoded"))).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> accountService.createAccount("12345678900", "wrong"));
        assertEquals("error.password_incorrect", exception.getMessageKey());
    }

    @Test
    @DisplayName("Should throw BusinessException when user already has 3 accounts")
    void createAccount_ShouldThrowBusinessExceptionWhenTooManyAccounts() {
        BankUser user = BankUser.builder().transactionPassword("encoded").build();
        when(bankUserRepository.findByCpf("12345678900")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq("encoded"))).thenReturn(true);
        when(accountRepository.findAllByCpf("12345678900")).thenReturn(Collections.nCopies(3, mock(Account.class)));

        BusinessException exception = assertThrows(BusinessException.class, () -> accountService.createAccount("12345678900", "1234"));
        assertEquals("error.account_limit", exception.getMessageKey());
    }

    @Test
    @DisplayName("Should perform transfer when accounts and password are valid")
    void transfer_ShouldTransferWhenValid() {
        BankUser holder = BankUser.builder().transactionPassword("encoded").build();
        Account origin = new Account("1", holder);
        origin.deposit(new BigDecimal("1000"));
        Account destination = new Account("2", BankUser.builder().build());

        when(accountRepository.findByAccountNumberAndActiveTrue("1")).thenReturn(Optional.of(origin));
        when(accountRepository.findByAccountNumberAndActiveTrue("2")).thenReturn(Optional.of(destination));
        when(passwordEncoder.matches(anyString(), eq("encoded"))).thenReturn(true);

        accountService.transfer("1", "2", new BigDecimal("500"), "1234");

        assertEquals(new BigDecimal("500"), origin.getBalance());
        assertEquals(new BigDecimal("500"), destination.getBalance());
        verify(accountRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Should throw ValidationException when transfer amount is non-positive")
    void transfer_ShouldThrowValidationExceptionWhenAmountNonPositive() {
        ValidationException exception = assertThrows(ValidationException.class, () -> accountService.transfer("1", "2", BigDecimal.ZERO, "1234"));
        assertEquals("error.amount_positive", exception.getMessageKey());
    }
}
