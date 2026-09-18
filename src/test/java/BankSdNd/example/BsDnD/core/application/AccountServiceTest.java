package BankSdNd.example.BsDnD.core.application;

import BankSdNd.example.BsDnD.core.domain.exception.*;
import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.service.AccountNumberGenerator;
import BankSdNd.example.BsDnD.core.port.out.AccountRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepositoryPort accountRepository;
    @Mock
    private BankUserRepositoryPort bankUserRepository;
    @Mock
    private AccountNumberGenerator accountNumberGenerator;
    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    private final String DEFAULT_CPF = "12345678900";
    private final char[] DEFAULT_TX_PASSWORD = "1234".toCharArray();
    private final String ENCODED_PASSWORD = "encoded";
    private final String GENERATED_ACCOUNT_NUMBER = "12345-6";
    private final String ORIGIN_ACCOUNT_NUM = "1";
    private final String DEST_ACCOUNT_NUM = "2";


    @Test
    @DisplayName("Should create an account when user and password are valid")
    void createAccount_ShouldCreateWhenValid() {

        BankUser user = BankUser.builder().transactionPassword(ENCODED_PASSWORD).build();
        when(bankUserRepository.findByCpf(DEFAULT_CPF)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq(ENCODED_PASSWORD))).thenReturn(true);
        when(accountRepository.findAllByCpf(DEFAULT_CPF)).thenReturn(Collections.emptyList());
        when(accountNumberGenerator.generateUniqueAccountNumber()).thenReturn(GENERATED_ACCOUNT_NUMBER);
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Account account = accountService.createAccount(DEFAULT_CPF, DEFAULT_TX_PASSWORD);

        assertNotNull(account);
        assertEquals(GENERATED_ACCOUNT_NUMBER, account.getAccountNumber());
        verify(accountRepository).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidTrasctionPasswordException when transaction password is wrong during creation")
    void createAccount_ShouldThrowInvalidTrasctionPasswordExceptionWhenPasswordWrong() {

        BankUser user = BankUser.builder().transactionPassword(ENCODED_PASSWORD).build();
        char[] wrongPassword = "wrong".toCharArray();
        when(bankUserRepository.findByCpf(DEFAULT_CPF)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq(ENCODED_PASSWORD))).thenReturn(false);

        InvalidTrasctionPasswordException exception = assertThrows(InvalidTrasctionPasswordException.class, () ->
                accountService.createAccount(DEFAULT_CPF, wrongPassword));

        assertEquals("error.password_incorrect", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw BusinessException when user already has 3 accounts")
    void createAccount_ShouldThrowBusinessExceptionWhenTooManyAccounts() {

        BankUser user = BankUser.builder().transactionPassword(ENCODED_PASSWORD).build();
        when(bankUserRepository.findByCpf(DEFAULT_CPF)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), eq(ENCODED_PASSWORD))).thenReturn(true);
        when(accountRepository.findAllByCpf(DEFAULT_CPF)).thenReturn(Collections.nCopies(3, mock(Account.class)));
        // criar um exception personalizada
        BusinessException exception = assertThrows(BusinessException.class, () ->
                accountService.createAccount(DEFAULT_CPF, DEFAULT_TX_PASSWORD));

        assertEquals("error.account_limit", exception.getMessage());
    }

    @Test
    @DisplayName("Should perform transfer when accounts and password are valid")
    void transfer_ShouldTransferWhenValid() {

        BigDecimal transferAmount = new BigDecimal("500");
        BigDecimal expectedOriginBalance = new BigDecimal("500");
        BigDecimal expectedDestBalance = new BigDecimal("500");
        BankUser holder = BankUser.builder().transactionPassword(ENCODED_PASSWORD).build();
        Account origin = new Account(ORIGIN_ACCOUNT_NUM, holder);
        origin.deposit(new BigDecimal("1000"));
        Account destination = new Account(DEST_ACCOUNT_NUM, BankUser.builder().build());

        when(accountRepository.findByAccountNumberAndActiveTrue(ORIGIN_ACCOUNT_NUM)).thenReturn(Optional.of(origin));
        when(accountRepository.findByAccountNumberAndActiveTrue(DEST_ACCOUNT_NUM)).thenReturn(Optional.of(destination));
        when(passwordEncoder.matches(any(), eq(ENCODED_PASSWORD))).thenReturn(true);

        accountService.transfer(ORIGIN_ACCOUNT_NUM, DEST_ACCOUNT_NUM, transferAmount, DEFAULT_TX_PASSWORD);

        assertEquals(0, expectedOriginBalance.compareTo(origin.getBalance()), "Origin balance mismatch");
        assertEquals(0, expectedDestBalance.compareTo(destination.getBalance()), "Destination balance mismatch");
        verify(accountRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Should successfully transfer using String password (Delegation Check)")
    void transfer_ShouldTransferWhenUsingStringPassword() {

        String stringPassword = "1234";
        BigDecimal tranferAmount = new BigDecimal("100");
        BigDecimal expectedBalance = new BigDecimal("100");

        BankUser holder = BankUser.builder().transactionPassword(ENCODED_PASSWORD).build();
        Account origin = new Account(ORIGIN_ACCOUNT_NUM, holder);
        origin.deposit(new BigDecimal("1000"));
        Account destination = new Account(DEST_ACCOUNT_NUM, BankUser.builder().build());

        when(accountRepository.findByAccountNumberAndActiveTrue(ORIGIN_ACCOUNT_NUM)).thenReturn(Optional.of(origin));
        when(accountRepository.findByAccountNumberAndActiveTrue(DEST_ACCOUNT_NUM)).thenReturn(Optional.of(destination));
        when(passwordEncoder.matches(any(), eq(ENCODED_PASSWORD))).thenReturn(true);

        accountService.transfer(ORIGIN_ACCOUNT_NUM, DEST_ACCOUNT_NUM, tranferAmount, stringPassword);

        assertEquals(0, expectedBalance.compareTo(destination.getBalance()));
    }

    @Test
    @DisplayName("Should throw ValidationException when transfer amount is non-positive")
    void transfer_ShouldThrowValidationExceptionWhenAmountNonPositive() {

        ValidationException exception = assertThrows(ValidationException.class, () ->
                accountService.transfer(ORIGIN_ACCOUNT_NUM, DEST_ACCOUNT_NUM, BigDecimal.ZERO, DEFAULT_TX_PASSWORD));
        assertEquals("error.amount_positive", exception.getMessage());
    }
}
