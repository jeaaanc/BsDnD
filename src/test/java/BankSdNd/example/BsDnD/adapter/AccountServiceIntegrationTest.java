package BankSdNd.example.BsDnD.adapter;

import BankSdNd.example.BsDnD.config.UserDetailsAdapter;
import BankSdNd.example.BsDnD.core.domain.model.Account;
import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import BankSdNd.example.BsDnD.core.domain.exception.*;
import BankSdNd.example.BsDnD.core.port.out.AccountRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.BankUserRepositoryPort;
import BankSdNd.example.BsDnD.core.port.out.PasswordEncoderPort;
import BankSdNd.example.BsDnD.core.application.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepositoryPort accountRepository;

    @Autowired
    private BankUserRepositoryPort bankUserRepository;

    @Autowired
    private PasswordEncoderPort passwordEncoder;

    @MockBean
    private CommandLineRunner commandLineRunner;

    private BankUser user1;
    private BankUser user2;
    private Account account1;
    private Account account2;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        bankUserRepository.deleteAll();

        user1 = BankUser.builder()
                .name("User 1")
                .cpf("11111111111")
                .income(new BigDecimal("2000"))
                .password(passwordEncoder.encode("password"))
                .transactionPassword(passwordEncoder.encode("1234"))
                .build();

        user2 = BankUser.builder()
                .name("User 2")
                .cpf("22222222222")
                .income(new BigDecimal("3000"))
                .password(passwordEncoder.encode("password"))
                .transactionPassword(passwordEncoder.encode("5678"))
                .build();

        user1 = bankUserRepository.save(user1);
        user2 = bankUserRepository.save(user2);

        account1 = new Account("10001", user1);
        account1.deposit(new BigDecimal("1000"));
        
        account2 = new Account("20001", user2);
        account2.deposit(new BigDecimal("500"));

        accountRepository.save(account1);
        accountRepository.save(account2);
    }

    @Test
    @DisplayName("Should transfer money between accounts correctly")
    void transferSuccessfully() {
        // Authenticate as user1
        var auth = new UsernamePasswordAuthenticationToken(new UserDetailsAdapter(user1), null, new UserDetailsAdapter(user1).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        accountService.transfer("10001", "20001", new BigDecimal("300"), "1234".toCharArray());

        // Then
        Account updatedOrigin = accountRepository.findByAccountNumberAndActiveTrue("10001").get();
        Account updatedDest = accountRepository.findByAccountNumberAndActiveTrue("20001").get();

        assertEquals(0, new BigDecimal("700.00").compareTo(updatedOrigin.getBalance()), "Origin balance mismatch");
        assertEquals(0, new BigDecimal("800.00").compareTo(updatedDest.getBalance()), "Destination balance mismatch");
    }

    @Test
    @DisplayName("Should throw exception when balance is insufficient")
    void transferWithInsufficientBalance() {
        // Authenticate as user1
        var auth = new UsernamePasswordAuthenticationToken(new UserDetailsAdapter(user1), null, new UserDetailsAdapter(user1).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Then
        assertThrows(InsufficientBalanceException.class, () -> {
            accountService.transfer("10001", "20001", new BigDecimal("1500"), "1234".toCharArray());
        });
    }

    @Test
    @DisplayName("Should throw exception when transaction password is wrong")
    void transferWithWrongPassword() {
        // Authenticate as user1
        var auth = new UsernamePasswordAuthenticationToken(new UserDetailsAdapter(user1), null, new UserDetailsAdapter(user1).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.transfer("10001", "20001", new BigDecimal("100"), "wrong".toCharArray());
        });
        
        assertTrue(exception.getMessage().contains("Invalid transaction password") || exception.getMessageKey().equals("error.password_incorrect"));
    }

    @Test
    @DisplayName("Should create a new account with zero balance")
    void accountCreateWithZeroBalance() {
        // Given
        // We need a user without an account for this test because of the new rule
        BankUser newUser = BankUser.builder()
                .name("New User")
                .cpf("33333333333")
                .password(passwordEncoder.encode("password"))
                .transactionPassword(passwordEncoder.encode("1234"))
                .build();
        newUser = bankUserRepository.save(newUser);

        var auth = new UsernamePasswordAuthenticationToken(new UserDetailsAdapter(newUser), null, new UserDetailsAdapter(newUser).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        Account account = accountService.createAccount("33333333333", "1234".toCharArray());

        // Then
        assertNotNull(account);
        assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));
    }
}
