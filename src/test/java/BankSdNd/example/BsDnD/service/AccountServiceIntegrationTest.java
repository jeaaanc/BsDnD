package BankSdNd.example.BsDnD.service;

import BankSdNd.example.BsDnD.domain.Account;
import BankSdNd.example.BsDnD.domain.BankUser;
import BankSdNd.example.BsDnD.exception.business.InsufficientBalanceException;
import BankSdNd.example.BsDnD.repository.AccountRepository;
import BankSdNd.example.BsDnD.repository.BankUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private AccountRepository accountRepository;

    @Autowired
    private BankUserRepository bankUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        user1 = new BankUser.Builder()
                .name("User 1")
                .cpf("11111111111")
                .income(new BigDecimal("2000"))
                .passWord(passwordEncoder.encode("password"))
                .transctionPassword(passwordEncoder.encode("1234"))
                .build();

        user2 = new BankUser.Builder()
                .name("User 2")
                .cpf("22222222222")
                .income(new BigDecimal("3000"))
                .passWord(passwordEncoder.encode("password"))
                .transctionPassword(passwordEncoder.encode("5678"))
                .build();

        bankUserRepository.save(user1);
        bankUserRepository.save(user2);

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
        var auth = new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        accountService.transfer("10001", "20001", new BigDecimal("300"), "1234");

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
        var auth = new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Then
        assertThrows(InsufficientBalanceException.class, () -> {
            accountService.transfer("10001", "20001", new BigDecimal("1500"), "1234");
        });
    }

    @Test
    @DisplayName("Should throw exception when transaction password is wrong")
    void transferWithWrongPassword() {
        // Authenticate as user1
        var auth = new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Then
        Exception exception = assertThrows(Exception.class, () -> {
            accountService.transfer("10001", "20001", new BigDecimal("100"), "wrong");
        });
        
        assertTrue(exception.getMessage().contains("Invalid transaction password"));
    }

    @Test
    @DisplayName("Should create a new account with zero balance")
    void accountCreateWithZeroBalance() {
        // Given
        String cpf = "11111111111";

        // When
        Account newAccount = accountService.accountCreate(cpf);

        // Then
        assertNotNull(newAccount);
        assertNotNull(newAccount.getAccountNumber());
        assertEquals(user1.getId(), newAccount.getHolder().getId());
        assertEquals(0, BigDecimal.ZERO.compareTo(newAccount.getBalance()));
    }
}
